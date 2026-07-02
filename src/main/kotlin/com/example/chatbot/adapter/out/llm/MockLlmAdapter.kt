package com.example.chatbot.adapter.out.llm

import com.example.chatbot.application.chat.port.out.LlmCompletion
import com.example.chatbot.application.chat.port.out.LlmPort
import com.example.chatbot.application.chat.port.out.LlmPrompt
import reactor.core.publisher.Flux

class MockLlmAdapter(
    private val defaultModel: String,
) : LlmPort {

    override suspend fun complete(prompt: LlmPrompt): LlmCompletion {
        val model = prompt.model ?: defaultModel
        return LlmCompletion(answerFor(prompt.question), model)
    }

    override fun stream(prompt: LlmPrompt): Flux<String> {
        val words = answerFor(prompt.question).split(" ")
        return Flux.fromIterable(words.mapIndexed { index, word -> if (index == 0) word else " $word" })
    }

    private fun answerFor(question: String): String =
        "[mock] \"$question\" 질문을 받았습니다. OPENAI_API_KEY 를 설정하면 실제 OpenAI 응답으로 전환됩니다."
}
