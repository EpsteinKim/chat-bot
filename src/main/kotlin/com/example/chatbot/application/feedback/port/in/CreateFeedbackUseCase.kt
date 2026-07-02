package com.example.chatbot.application.feedback.port.`in`

import com.example.chatbot.domain.feedback.FeedbackStatus
import java.time.Instant

interface CreateFeedbackUseCase {
    fun create(command: CreateFeedbackCommand): FeedbackResult
}

data class CreateFeedbackCommand(
    val userId: String,
    val isAdmin: Boolean,
    val chatId: String,
    val positive: Boolean,
)

data class FeedbackResult(
    val id: String,
    val userId: String,
    val chatId: String,
    val positive: Boolean,
    val status: FeedbackStatus,
    val createdAt: Instant,
)
