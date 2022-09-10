package tw.kotlin.application

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.title

private val dataMap = mutableMapOf<String, User>()

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/mopcon-2022-kmm.js") {}
    }
}

suspend fun signup(call: ApplicationCall) =
    run {
        val userSignupReqDTO = call.receive<UserSignupReqDTO>()
        if (dataMap.containsKey(userSignupReqDTO.username)) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Username Existed"))
        } else {
            dataMap[userSignupReqDTO.username] = User(
                username = userSignupReqDTO.username,
                passwordHash = userSignupReqDTO.password.md5(),
                secret = createSecret(),
                createDate = Clock.System.now().toLocalDateTime(TimeZone.UTC),
            )

            call.respond(HttpStatusCode.OK, dataMap[userSignupReqDTO.username]!!.toResp())
        }
    }

suspend fun qrcode(call: ApplicationCall) =
    run {
        val user = dataMap[call.request.queryParameters["username"]]!!

        val bytes = createQrCodeImgBytes(
            username = user.username,
            secret = user.secret,
        )

        call.respondBytes(
            bytes = bytes,
            contentType = ContentType.Image.PNG,
            status = HttpStatusCode.OK,
        )
    }

suspend fun login(call: ApplicationCall) =
    run {
        val userLoginReqDTO = call.receive<UserLoginReqDTO>()

        if (!dataMap.containsKey(userLoginReqDTO.username)) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Username / Password Not Matched"))
        }

        val user = dataMap[userLoginReqDTO.username]!!
        if (userLoginReqDTO.password.md5() != user.passwordHash) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Username / Password Not Matched"))
        }

        if (!verifyCode(user.secret, userLoginReqDTO.code)) {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Wrong 2FA Code"))
        }

        call.respond(HttpStatusCode.OK, user.toResp())
    }

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
            post("/user/signup") {
                signup(call)
            }
            get("/user/qrcode") {
                qrcode(call)
            }
            post("/user/login") {
                login(call)
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}