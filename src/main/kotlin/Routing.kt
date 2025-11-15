package com.dialogai

import com.dialogai.util.encode
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun Application.configureRouting() {
    routing {
        // Сначала — обработка проверки Let's Encrypt
        get("/.well-known/acme-challenge/{token}") {
            val token = call.parameters["token"]
            if (token == "tHBk4cAEQvAaLRdVtGFBFhJdYPGRYS_stvXZjgi0CEU") {
                call.respondText("tHBk4cAEQvAaLRdVtGFBFhJdYPGRYS_stvXZjgi0CEU.5gsoEYCibxdw0cBYEEpOkwlay7OKufNO9weuRSfJcb0")
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/") {
            println("Received Hello World!")
            call.respondText("Hello World!")
        }
        post("/check-password") {
            try {
                val request = call.receive<PasswordRequest>()
                val isValid = request.password == "12345"

                val response = PasswordResponse(
                    correct = isValid,
                    message = if (isValid) "Пароль верный" else "Пароль неверный"
                )
                call.respond(response)
            } catch (e: Exception) {
                println("error e=${e.message}")
                call.respond(PasswordResponse(correct = false, message = "Ошибка: неверный формат запроса"))
            }
        }
        post("api/auth/get-code-for-new-pin") {
            try {
                val request = call.receive<CheckLoginRequest>()
                println("CheckLoginRequest=$request")

                // Генерируем 4-значный код
                val code = generateCode()
                println("Generated code: $code")

                // Отправляем код на email
                val emailSent = sendEmail(
                    toEmail = request.login,
                    code = code
                )

                if (emailSent) {
                    println("Email sent successfully to ${request.login}")
                } else {
                    println("Failed to send email to ${request.login}")
                }

                val response = CheckLoginResponse(code = encode(code))
                call.respond(response)
            } catch (e: Exception) {
                println("Error in check-login: ${e.message}")
                call.respond(CheckLoginResponse(code = encode("1234")))
            }
        }
    }
}

// Функция для генерации 4-значного кода
fun generateCode(): String {
    val random = Random()
    return String.format("%04d", random.nextInt(10000))
}

// Функция для отправки email
fun sendEmail(toEmail: String, code: String): Boolean {
    return try {
        val fromEmail = "rufat0104216@gmail.com"
        // Вам нужно использовать App Password вместо обычного пароля
        // Настройте App Password в настройках аккаунта Google
        val password = "pvwsjrabxfrxmpgq" // ЗАМЕНИТЕ на реальный App Password

        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(fromEmail, password)
            }
        })

        val message = MimeMessage(session)
        message.setFrom(InternetAddress(fromEmail))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
        message.subject = "Диалоги - Код для смены ПИН-кода"
        message.setText("""
            Для смены ПИН-кода вот вам одноразовый код: $code
            Введите его на экране приложения Диалоги и сможете придумать новый ПИН.
        """.trimIndent())

        Transport.send(message)
        true
    } catch (e: Exception) {
        println("Error sending email: ${e.message}")
        false
    }
}

@Serializable
data class PasswordRequest(val password: String)

@Serializable
data class PasswordResponse(val correct: Boolean, val message: String)

@Serializable
data class CheckLoginRequest(val login: String)

@Serializable
data class CheckLoginResponse(val code: String)