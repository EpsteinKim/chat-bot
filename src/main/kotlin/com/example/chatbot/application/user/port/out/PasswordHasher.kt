package com.example.chatbot.application.user.port.out

interface PasswordHasher {
    fun hash(rawPassword: String): String
    fun matches(rawPassword: String, hash: String): Boolean
}
