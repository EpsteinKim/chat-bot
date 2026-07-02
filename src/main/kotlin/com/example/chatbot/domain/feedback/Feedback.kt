package com.example.chatbot.domain.feedback

import java.time.Instant

data class Feedback(
    val id: String,
    val userId: String,
    val chatId: String,
    val positive: Boolean,
    val status: FeedbackStatus,
    val createdAt: Instant,
) {
    fun withStatus(newStatus: FeedbackStatus): Feedback = copy(status = newStatus)
}
