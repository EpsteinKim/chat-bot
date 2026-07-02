package com.example.chatbot.domain.user

import java.time.Instant

data class User(
    val id: String,
    val email: String,
    val passwordHash: String,
    val name: String,
    val role: Role,
    val createdAt: Instant,
)
