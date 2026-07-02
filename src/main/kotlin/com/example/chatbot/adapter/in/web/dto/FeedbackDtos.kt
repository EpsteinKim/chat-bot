package com.example.chatbot.adapter.`in`.web.dto

import com.example.chatbot.application.common.PageResult
import com.example.chatbot.application.feedback.port.`in`.FeedbackResult
import com.example.chatbot.domain.feedback.FeedbackStatus
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class CreateFeedbackRequest(
    @field:NotBlank
    val chatId: String,
    @param:JsonProperty("isPositive")
    @get:JsonProperty("isPositive")
    val positive: Boolean,
)

data class UpdateFeedbackStatusRequest(
    val status: FeedbackStatus,
)

data class FeedbackResponse(
    val id: String,
    val userId: String,
    val chatId: String,
    @get:JsonProperty("isPositive")
    val positive: Boolean,
    val status: FeedbackStatus,
    val createdAt: Instant,
) {
    companion object {
        fun from(r: FeedbackResult): FeedbackResponse =
            FeedbackResponse(r.id, r.userId, r.chatId, r.positive, r.status, r.createdAt)
    }
}

data class FeedbackPageResponse(
    val items: List<FeedbackResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
) {
    companion object {
        fun from(result: PageResult<FeedbackResult>): FeedbackPageResponse =
            FeedbackPageResponse(
                result.items.map { FeedbackResponse.from(it) },
                result.page,
                result.size,
                result.totalElements,
                result.totalPages,
            )
    }
}
