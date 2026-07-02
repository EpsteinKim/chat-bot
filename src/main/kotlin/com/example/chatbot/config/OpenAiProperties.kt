package com.example.chatbot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.openai")
data class OpenAiProperties(
    val apiKey: String = "",
    val baseUrl: String = "https://api.openai.com/v1",
    val defaultModel: String = "gpt-4o-mini",
) {
    val enabled: Boolean get() = apiKey.isNotBlank()
}
