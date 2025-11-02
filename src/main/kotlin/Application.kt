package com.dialogai

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import org.slf4j.event.Level

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        json() // Активируем JSON-сериализацию
    }
    configureRouting()
}
