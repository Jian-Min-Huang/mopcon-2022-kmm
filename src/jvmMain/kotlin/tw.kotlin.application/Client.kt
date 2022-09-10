package tw.kotlin.application

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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

suspend fun signup(userSignupReqDTO: UserSignupReqDTO): User =
    httpClient.post("/user/signup") {
        setBody(userSignupReqDTO)
        contentType(ContentType.Application.Json)
    }.body()

suspend fun qrcode(username: String): ByteArray =
    httpClient.get("/user/qrcode") {
        parameter("username", username)
        contentType(ContentType.Image.PNG)
    }.body()

suspend fun login(userLoginReqDTO: UserLoginReqDTO): User =
    httpClient.post("/user/login") {
        setBody(userLoginReqDTO)
        contentType(ContentType.Application.Json)
    }.body()
