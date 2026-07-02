package com.example.chatbot.application.chat.port.`in`

import reactor.core.publisher.Flux
import java.time.Instant

interface CreateChatUseCase {
    suspend fun create(command: CreateChatCommand): CreateChatResult
    fun stream(command: CreateChatCommand): Flux<String>
}

data class CreateChatCommand(
    val userId: String,
    val question: String,
    val model: String? = null,
)

data class CreateChatResult(
    val chatId: String,
    val threadId: String,
    val question: String,
    val answer: String,
    val model: String,
    val createdAt: Instant,
)
