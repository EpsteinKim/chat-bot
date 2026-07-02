package com.example.chatbot.application.activity.port.`in`

import java.time.Instant

interface ActivityStatsUseCase {
    fun stats(isAdmin: Boolean): ActivityStatsResult
}

data class ActivityStatsResult(
    val signupCount: Long,
    val loginCount: Long,
    val chatCreatedCount: Long,
    val from: Instant,
    val to: Instant,
)
