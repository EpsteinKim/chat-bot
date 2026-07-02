package com.example.chatbot.application.chat.port.out

import reactor.core.publisher.Flux

interface LlmPort {
    suspend fun complete(prompt: LlmPrompt): LlmCompletion
    fun stream(prompt: LlmPrompt): Flux<String>
}

data class LlmPrompt(
    val question: String,
    val model: String?,
    val history: List<LlmTurn> = emptyList(),
    val context: List<String> = emptyList(),
)

data class LlmTurn(
    val question: String,
    val answer: String,
)

data class LlmCompletion(
    val answer: String,
    val model: String,
)
