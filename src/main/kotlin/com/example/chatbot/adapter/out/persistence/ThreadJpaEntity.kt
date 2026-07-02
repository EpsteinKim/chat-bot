package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.domain.chat.ChatThread
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "threads")
class ThreadJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant,
) {
    fun toDomain(): ChatThread = ChatThread(id, userId, createdAt, updatedAt)

    companion object {
        fun fromDomain(thread: ChatThread): ThreadJpaEntity =
            ThreadJpaEntity(thread.id, thread.userId, thread.createdAt, thread.updatedAt)
    }
}
