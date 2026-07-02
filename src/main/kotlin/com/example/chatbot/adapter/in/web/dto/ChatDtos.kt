package com.example.chatbot.adapter.`in`.web.dto

import com.example.chatbot.application.chat.port.`in`.CreateChatResult
import com.example.chatbot.application.chat.port.`in`.ThreadView
import com.example.chatbot.application.common.PageResult
import com.example.chatbot.domain.chat.Chat
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class CreateChatRequest(
    @field:NotBlank(message = "question must not be blank")
    val question: String,
    val model: String? = null,
    val isStreaming: Boolean = false,
)

data class CreateChatResponse(
    val chatId: String,
    val threadId: String,
    val question: String,
    val answer: String,
    val model: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(result: CreateChatResult): CreateChatResponse =
            CreateChatResponse(
                result.chatId,
                result.threadId,
                result.question,
                result.answer,
                result.model,
                result.createdAt,
            )
    }
}

data class ChatItemResponse(
    val chatId: String,
    val question: String,
    val answer: String,
    val model: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(chat: Chat): ChatItemResponse =
            ChatItemResponse(chat.id, chat.question, chat.answer, chat.model, chat.createdAt)
    }
}

data class ThreadResponse(
    val threadId: String,
    val userId: String,
    val chats: List<ChatItemResponse>,
) {
    companion object {
        fun from(view: ThreadView): ThreadResponse =
            ThreadResponse(view.threadId, view.userId, view.chats.map { ChatItemResponse.from(it) })
    }
}

data class ThreadPageResponse(
    val items: List<ThreadResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
) {
    companion object {
        fun from(result: PageResult<ThreadView>): ThreadPageResponse =
            ThreadPageResponse(
                result.items.map { ThreadResponse.from(it) },
                result.page,
                result.size,
                result.totalElements,
                result.totalPages,
            )
    }
}
