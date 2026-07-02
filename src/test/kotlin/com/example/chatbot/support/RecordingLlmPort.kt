package com.example.chatbot.support

import com.example.chatbot.application.chat.port.out.LlmCompletion
import com.example.chatbot.application.chat.port.out.LlmPort
import com.example.chatbot.application.chat.port.out.LlmPrompt
import reactor.core.publisher.Flux

class RecordingLlmPort(
    private val cannedAnswer: String = "canned answer",
) : LlmPort {
    var lastPrompt: LlmPrompt? = null
        private set

    override suspend fun complete(prompt: LlmPrompt): LlmCompletion {
        lastPrompt = prompt
        return LlmCompletion(cannedAnswer, prompt.model ?: "default-model")
    }

    override fun stream(prompt: LlmPrompt): Flux<String> {
        lastPrompt = prompt
        val words = cannedAnswer.split(" ")
        return Flux.fromIterable(words.mapIndexed { index, word -> if (index == 0) word else " $word" })
    }
}
