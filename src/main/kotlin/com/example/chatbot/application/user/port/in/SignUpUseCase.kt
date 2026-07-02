package com.example.chatbot.application.user.port.`in`

import com.example.chatbot.domain.user.Role
import java.time.Instant

interface SignUpUseCase {
    fun signUp(command: SignUpCommand): SignUpResult
}

data class SignUpCommand(
    val email: String,
    val password: String,
    val name: String,
)

data class SignUpResult(
    val id: String,
    val email: String,
    val name: String,
    val role: Role,
    val createdAt: Instant,
)
