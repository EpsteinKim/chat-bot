package com.example.chatbot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.jwt")
data class JwtProperties(
    val secret: String = "",
    val expirationMs: Long = 86_400_000,
)
