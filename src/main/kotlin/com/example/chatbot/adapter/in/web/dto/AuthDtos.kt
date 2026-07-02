package com.example.chatbot.adapter.`in`.web.dto

import com.example.chatbot.application.user.port.`in`.LoginResult
import com.example.chatbot.application.user.port.`in`.SignUpResult
import com.example.chatbot.domain.user.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class SignUpRequest(
    @field:Email(message = "email must be valid")
    @field:NotBlank(message = "email must not be blank")
    val email: String,
    @field:NotBlank(message = "password must not be blank")
    @field:Size(min = 8, message = "password must be at least 8 characters")
    val password: String,
    @field:NotBlank(message = "name must not be blank")
    val name: String,
)

data class SignUpResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: Role,
    val createdAt: Instant,
) {
    companion object {
        fun from(result: SignUpResult): SignUpResponse =
            SignUpResponse(result.id, result.email, result.name, result.role, result.createdAt)
    }
}

data class LoginRequest(
    @field:Email(message = "email must be valid")
    @field:NotBlank(message = "email must not be blank")
    val email: String,
    @field:NotBlank(message = "password must not be blank")
    val password: String,
)

data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresInMs: Long,
) {
    companion object {
        fun from(result: LoginResult): LoginResponse =
            LoginResponse(result.accessToken, result.tokenType, result.expiresInMs)
    }
}
