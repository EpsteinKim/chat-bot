package com.example.chatbot.application.user.port.out

import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import java.time.Instant

interface TokenProvider {
    fun issue(user: User): TokenBundle
    fun parse(token: String): TokenClaims
}

data class TokenBundle(
    val accessToken: String,
    val tokenType: String,
    val expiresInMs: Long,
    val expiresAt: Instant,
)

data class TokenClaims(
    val userId: String,
    val email: String,
    val role: Role,
    val expiresAt: Instant,
)
