package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.domain.activity.ActivityLog
import com.example.chatbot.domain.activity.ActivityType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "activity_logs")
class ActivityLogJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: ActivityType,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
) {
    fun toDomain(): ActivityLog = ActivityLog(id, userId, type, createdAt)

    companion object {
        fun fromDomain(log: ActivityLog): ActivityLogJpaEntity =
            ActivityLogJpaEntity(log.id, log.userId, log.type, log.createdAt)
    }
}
