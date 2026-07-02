package com.example.chatbot.application.activity.port.`in`

import com.example.chatbot.domain.activity.ActivityType

interface RecordActivityUseCase {
    fun record(userId: String, type: ActivityType)
}
