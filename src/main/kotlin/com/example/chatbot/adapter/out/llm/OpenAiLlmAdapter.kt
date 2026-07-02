package com.example.chatbot.adapter.out.llm

import com.example.chatbot.application.chat.port.out.LlmCompletion
import com.example.chatbot.application.chat.port.out.LlmPort
import com.example.chatbot.application.chat.port.out.LlmPrompt
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Flux

class OpenAiLlmAdapter(
    private val webClient: WebClient,
    private val defaultModel: String,
    private val objectMapper: ObjectMapper,
) : LlmPort {

    override suspend fun complete(prompt: LlmPrompt): LlmCompletion {
        val model = prompt.model ?: defaultModel
        val response = webClient.post()
            .uri("/chat/completions")
            .bodyValue(requestBody(model, prompt, stream = false))
            .retrieve()
            .awaitBody<String>()
        val node = objectMapper.readTree(response)
        val content = node.path("choices").path(0).path("message").path("content").asText("")
        return LlmCompletion(content, model)
    }

    override fun stream(prompt: LlmPrompt): Flux<String> {
        val model = prompt.model ?: defaultModel
        return webClient.post()
            .uri("/chat/completions")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .bodyValue(requestBody(model, prompt, stream = true))
            .retrieve()
            .bodyToFlux<String>()
            .takeUntil { it.trim() == "[DONE]" }
            .mapNotNull { extractDelta(it) }
    }

    private fun requestBody(model: String, prompt: LlmPrompt, stream: Boolean): Map<String, Any> =
        mapOf(
            "model" to model,
            "stream" to stream,
            "messages" to buildMessages(prompt),
        )

    private fun buildMessages(prompt: LlmPrompt): List<Map<String, String>> {
        val messages = mutableListOf<Map<String, String>>()
        messages += mapOf("role" to "system", "content" to systemPrompt(prompt))
        prompt.history.forEach { turn ->
            messages += mapOf("role" to "user", "content" to turn.question)
            messages += mapOf("role" to "assistant", "content" to turn.answer)
        }
        messages += mapOf("role" to "user", "content" to prompt.question)
        return messages
    }

    private fun systemPrompt(prompt: LlmPrompt): String {
        val base = "You are a helpful assistant."
        if (prompt.context.isEmpty()) return base
        val references = prompt.context.joinToString("\n---\n")
        return "$base\nUse the following reference documents when relevant:\n$references"
    }

    private fun extractDelta(raw: String): String? {
        val payload = raw.removePrefix("data:").trim()
        if (payload.isEmpty() || payload == "[DONE]") return null
        val node = objectMapper.readTree(payload)
        val content = node.path("choices").path(0).path("delta").path("content").asText("")
        return content.ifEmpty { null }
    }
}
