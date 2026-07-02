package com.example.chatbot.application.feedback.port.`in`

import com.example.chatbot.application.common.PageResult

interface QueryFeedbackUseCase {
    fun list(query: ListFeedbackQuery): PageResult<FeedbackResult>
}

data class ListFeedbackQuery(
    val userId: String,
    val isAdmin: Boolean,
    val positive: Boolean? = null,
    val ascending: Boolean = false,
    val page: Int = 0,
    val size: Int = 20,
)
