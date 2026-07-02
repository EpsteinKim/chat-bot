package com.example.chatbot.adapter.`in`.web

import com.example.chatbot.security.AuthenticatedUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

data class RequestUser(
    val userId: String,
    val isAdmin: Boolean,
)

@Component
class CurrentUser {
    fun resolve(): RequestUser {
        val principal = SecurityContextHolder.getContext().authentication?.principal as? AuthenticatedUser
            ?: throw IllegalStateException("no authenticated user in security context")
        return RequestUser(principal.userId, principal.isAdmin)
    }
}
