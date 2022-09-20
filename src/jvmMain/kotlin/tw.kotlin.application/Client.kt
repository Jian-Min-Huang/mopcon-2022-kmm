package tw.kotlin.application

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

val httpClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
    defaultRequest {
        url("http://localhost:8080")
    }
    expectSuccess = true
}

suspend fun signup(userSignupReqDTO: UserSignupReqDTO): HttpResponse =
    httpClient.post("/user/signup") {
        setBody(userSignupReqDTO)
        contentType(ContentType.Application.Json)
    }

suspend fun qrcode(username: String): HttpResponse =
    httpClient.get("/user/qrcode") {
        parameter("username", username)
        contentType(ContentType.Image.PNG)
    }

fun qrcodeLink(username: String): String =
    "http://localhost:8080/user/qrcode?$username"

suspend fun login(userLoginReqDTO: UserLoginReqDTO): HttpResponse =
    httpClient.post("/user/login") {
        setBody(userLoginReqDTO)
        contentType(ContentType.Application.Json)
    }
