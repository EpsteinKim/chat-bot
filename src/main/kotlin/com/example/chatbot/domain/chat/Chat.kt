package com.example.chatbot.domain.chat

import java.time.Instant

data class Chat(
    val id: String,
    val threadId: String,
    val userId: String,
    val question: String,
    val answer: String,
    val model: String,
    val createdAt: Instant,
)
