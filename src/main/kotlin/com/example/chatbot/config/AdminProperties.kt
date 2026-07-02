package com.example.chatbot.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.admin")
data class AdminProperties(
    val email: String = "admin@chatbot.local",
    val password: String = "admin1234",
    val name: String = "Administrator",
)
