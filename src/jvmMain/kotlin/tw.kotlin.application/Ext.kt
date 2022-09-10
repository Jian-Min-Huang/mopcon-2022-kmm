package tw.kotlin.application

import java.security.MessageDigest
import java.util.Base64

fun String.md5(): String =
    MessageDigest.getInstance("MD5")
        .also {
            it.update(toByteArray())
        }.run {
            String(Base64.getEncoder().encode(digest()))
        }

fun User.toResp(): UserRespDTO =
    UserRespDTO(
        username = username,
        createDate = createDate,
    )
