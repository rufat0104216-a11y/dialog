package com.dialogai

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
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
                call.respond(PasswordResponse(correct = false, message = "Ошибка: неверный формат запроса"))
            }
        }
    }
}

@Serializable
data class PasswordRequest(val password: String)

@Serializable
data class PasswordResponse(val correct: Boolean, val message: String)
