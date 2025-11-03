package com.dialogai

import com.dialogai.util.encode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    routing {
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
                // Всегда возвращаем "1234" в ответе
                val response = CheckLoginResponse(code = encode("1234"))
                call.respond(response)
            } catch (e: Exception) {
                println("Error in check-login: ${e.message}")
                call.respond(CheckLoginResponse(code = encode("1234"))) // Даже при ошибке возвращаем "1234"
            }
        }
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
