package com.example.chatbot.application.user.port.`in`

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}

data class LoginCommand(
    val email: String,
    val password: String,
)

data class LoginResult(
    val accessToken: String,
    val tokenType: String,
    val expiresInMs: Long,
)
