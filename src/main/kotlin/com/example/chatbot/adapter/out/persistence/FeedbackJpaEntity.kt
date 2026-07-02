package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.domain.feedback.Feedback
import com.example.chatbot.domain.feedback.FeedbackStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "feedbacks")
class FeedbackJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "chat_id", nullable = false)
    val chatId: String,

    @Column(name = "is_positive", nullable = false)
    val isPositive: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: FeedbackStatus,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
) {
    fun toDomain(): Feedback = Feedback(id, userId, chatId, isPositive, status, createdAt)

    companion object {
        fun fromDomain(feedback: Feedback): FeedbackJpaEntity =
            FeedbackJpaEntity(feedback.id, feedback.userId, feedback.chatId, feedback.positive, feedback.status, feedback.createdAt)
    }
}
