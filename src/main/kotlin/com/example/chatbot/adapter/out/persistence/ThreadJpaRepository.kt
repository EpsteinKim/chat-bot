package com.example.chatbot.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ThreadJpaRepository : JpaRepository<ThreadJpaEntity, String> {
    fun findFirstByUserIdOrderByUpdatedAtDesc(userId: String): ThreadJpaEntity?
    fun findByUserId(userId: String): List<ThreadJpaEntity>
    fun findByUserId(userId: String, pageable: Pageable): Page<ThreadJpaEntity>
}
