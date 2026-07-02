package com.example.chatbot.application.activity.port.out

import com.example.chatbot.domain.activity.ActivityLog
import com.example.chatbot.domain.activity.ActivityType
import java.time.Instant

interface ActivityLogRepository {
    fun save(log: ActivityLog): ActivityLog
    fun countByTypeAndCreatedAtAfter(type: ActivityType, threshold: Instant): Long
}
