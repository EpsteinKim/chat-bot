package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.domain.activity.ActivityType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ActivityLogJpaRepository : JpaRepository<ActivityLogJpaEntity, String> {
    fun countByTypeAndCreatedAtAfter(type: ActivityType, threshold: Instant): Long
}
