package com.example.chatbot.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ChatJpaRepository : JpaRepository<ChatJpaEntity, String> {
    fun findByThreadId(threadId: String): List<ChatJpaEntity>
    fun findByUserId(userId: String): List<ChatJpaEntity>
    fun findByCreatedAtAfter(threshold: Instant): List<ChatJpaEntity>
    fun deleteByThreadId(threadId: String)
}
