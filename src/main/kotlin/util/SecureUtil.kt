package com.dialogai.util

import java.util.Base64

fun encode(pin: String): String {
    try {
        return  Base64.getEncoder().encodeToString(pin.toByteArray())
    } catch (e: Exception) {
        println("encode error=${e.message}")
    }
    return ""
}

fun decode(encoded: String): String {
    try {
        return String(Base64.getDecoder().decode(encoded))
    } catch (e: Exception) {
        println("decode error=${e.message}")
    }
    return ""
}