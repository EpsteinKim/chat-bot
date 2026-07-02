package com.example.chatbot.adapter.`in`.web.dto

import com.example.chatbot.application.activity.port.`in`.ActivityStatsResult
import java.time.Instant

data class ActivityStatsResponse(
    val signupCount: Long,
    val loginCount: Long,
    val chatCreatedCount: Long,
    val from: Instant,
    val to: Instant,
) {
    companion object {
        fun from(r: ActivityStatsResult): ActivityStatsResponse =
            ActivityStatsResponse(r.signupCount, r.loginCount, r.chatCreatedCount, r.from, r.to)
    }
}
