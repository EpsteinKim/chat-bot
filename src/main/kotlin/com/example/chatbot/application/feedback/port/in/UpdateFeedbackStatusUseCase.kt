package com.example.chatbot.application.feedback.port.`in`

import com.example.chatbot.domain.feedback.FeedbackStatus

interface UpdateFeedbackStatusUseCase {
    fun updateStatus(command: UpdateFeedbackStatusCommand): FeedbackResult
}

data class UpdateFeedbackStatusCommand(
    val isAdmin: Boolean,
    val feedbackId: String,
    val status: FeedbackStatus,
)
