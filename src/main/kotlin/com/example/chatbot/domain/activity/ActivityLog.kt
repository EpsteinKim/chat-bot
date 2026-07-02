package com.example.chatbot.domain.activity

import java.time.Instant

data class ActivityLog(
    val id: String,
    val userId: String,
    val type: ActivityType,
    val createdAt: Instant,
)
