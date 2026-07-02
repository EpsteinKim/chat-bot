package com.example.chatbot.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackJpaRepository : JpaRepository<FeedbackJpaEntity, String> {
    fun existsByUserIdAndChatId(userId: String, chatId: String): Boolean
    fun findByUserId(userId: String, pageable: Pageable): Page<FeedbackJpaEntity>
    fun findByIsPositive(isPositive: Boolean, pageable: Pageable): Page<FeedbackJpaEntity>
    fun findByUserIdAndIsPositive(userId: String, isPositive: Boolean, pageable: Pageable): Page<FeedbackJpaEntity>
}
