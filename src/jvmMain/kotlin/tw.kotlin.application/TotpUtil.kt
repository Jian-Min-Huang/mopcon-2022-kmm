package tw.kotlin.application

import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.code.HashingAlgorithm
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider

private val secretGenerator = DefaultSecretGenerator(64)
private val systemTimeProvider = SystemTimeProvider()
private val defaultCodeGenerator = DefaultCodeGenerator(HashingAlgorithm.SHA512)
private val defaultCodeVerifier = DefaultCodeVerifier(defaultCodeGenerator, systemTimeProvider)

fun createSecret(): String = secretGenerator.generate()

fun createQrCodeImgBytes(
    username: String,
    secret: String,
): ByteArray =
    QrData
        .Builder()
        .label(username)
        .secret(secret)
        .issuer("MOPCON-2022-KMM")
        .algorithm(HashingAlgorithm.SHA512)
        .digits(6)
        .period(30)
        .build()
        .run { ZxingPngQrGenerator().generate(this) }

fun verifyCode(
    secret: String,
    code: String,
): Boolean =
    defaultCodeVerifier
        .isValidCode(secret, code)