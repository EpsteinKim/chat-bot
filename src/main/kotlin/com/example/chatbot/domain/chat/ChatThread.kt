package com.example.chatbot.domain.chat

import java.time.Instant

data class ChatThread(
    val id: String,
    val userId: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun isActiveAt(moment: Instant, idleMinutes: Long): Boolean {
        val elapsed = java.time.Duration.between(updatedAt, moment).toMinutes()
        return elapsed < idleMinutes
    }

    fun touched(moment: Instant): ChatThread = copy(updatedAt = moment)
}
