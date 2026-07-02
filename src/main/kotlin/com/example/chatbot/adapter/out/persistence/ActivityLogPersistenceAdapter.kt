package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.application.activity.port.out.ActivityLogRepository
import com.example.chatbot.domain.activity.ActivityLog
import com.example.chatbot.domain.activity.ActivityType
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class ActivityLogPersistenceAdapter(
    private val jpaRepository: ActivityLogJpaRepository,
) : ActivityLogRepository {

    override fun save(log: ActivityLog): ActivityLog =
        jpaRepository.save(ActivityLogJpaEntity.fromDomain(log)).toDomain()

    override fun countByTypeAndCreatedAtAfter(type: ActivityType, threshold: Instant): Long =
        jpaRepository.countByTypeAndCreatedAtAfter(type, threshold)
}
