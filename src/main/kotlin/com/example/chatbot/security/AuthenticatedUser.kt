package com.example.chatbot.security

import com.example.chatbot.domain.user.Role

data class AuthenticatedUser(
    val userId: String,
    val email: String,
    val role: Role,
) {
    val isAdmin: Boolean get() = role == Role.ADMIN
}
