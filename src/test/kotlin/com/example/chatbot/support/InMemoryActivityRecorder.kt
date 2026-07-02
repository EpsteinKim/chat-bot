package com.example.chatbot.support

import com.example.chatbot.application.activity.port.`in`.RecordActivityUseCase
import com.example.chatbot.domain.activity.ActivityType

class InMemoryActivityRecorder : RecordActivityUseCase {
    val recorded = mutableListOf<Pair<String, ActivityType>>()

    override fun record(userId: String, type: ActivityType) {
        recorded += userId to type
    }
}
