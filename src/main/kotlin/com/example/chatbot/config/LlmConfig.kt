package com.example.chatbot.config

import com.example.chatbot.adapter.out.llm.MockLlmAdapter
import com.example.chatbot.adapter.out.llm.OpenAiLlmAdapter
import com.example.chatbot.application.chat.port.out.LlmPort
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class LlmConfig {

    private val log = LoggerFactory.getLogger(LlmConfig::class.java)

    @Bean
    fun llmPort(properties: OpenAiProperties, objectMapper: ObjectMapper): LlmPort {
        if (!properties.enabled) {
            log.warn("OPENAI_API_KEY not set — using MockLlmAdapter. Set the key to call the real OpenAI API.")
            return MockLlmAdapter(properties.defaultModel)
        }
        log.info("OpenAI adapter enabled (model={}, baseUrl={})", properties.defaultModel, properties.baseUrl)
        val webClient = WebClient.builder()
            .baseUrl(properties.baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${properties.apiKey}")
            .build()
        return OpenAiLlmAdapter(webClient, properties.defaultModel, objectMapper)
    }
}
