package tw.kotlin.application

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UserSignupReqDTO(
    val username: String,
    val password: String,
)

@Serializable
data class UserLoginReqDTO(
    val username: String,
    val password: String,
    val code: String,
)

@Serializable
data class UserRespDTO(
    val username: String,
    val createDate: LocalDateTime,
)

@Serializable
data class User(
    val username: String,
    val passwordHash: String,
    val secret: String,
    val createDate: LocalDateTime,
)
