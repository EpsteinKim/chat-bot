package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.domain.chat.Chat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "chats")
class ChatJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "thread_id", nullable = false)
    val threadId: String,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "question", columnDefinition = "text", nullable = false)
    val question: String,

    @Column(name = "answer", columnDefinition = "text", nullable = false)
    val answer: String,

    @Column(name = "model", nullable = false)
    val model: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
) {
    fun toDomain(): Chat = Chat(id, threadId, userId, question, answer, model, createdAt)

    companion object {
        fun fromDomain(chat: Chat): ChatJpaEntity =
            ChatJpaEntity(chat.id, chat.threadId, chat.userId, chat.question, chat.answer, chat.model, chat.createdAt)
    }
}
