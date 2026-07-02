package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.application.chat.port.out.ChatRepository
import com.example.chatbot.domain.chat.Chat
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
class ChatPersistenceAdapter(
    private val jpaRepository: ChatJpaRepository,
) : ChatRepository {

    override fun save(chat: Chat): Chat =
        jpaRepository.save(ChatJpaEntity.fromDomain(chat)).toDomain()

    override fun findById(id: String): Chat? =
        jpaRepository.findById(id).map { it.toDomain() }.orElse(null)

    override fun findByThreadId(threadId: String): List<Chat> =
        jpaRepository.findByThreadId(threadId).map { it.toDomain() }

    override fun findByUserId(userId: String): List<Chat> =
        jpaRepository.findByUserId(userId).map { it.toDomain() }

    override fun findAll(): List<Chat> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun findByCreatedAtAfter(threshold: Instant): List<Chat> =
        jpaRepository.findByCreatedAtAfter(threshold).map { it.toDomain() }

    @Transactional
    override fun deleteByThreadId(threadId: String) =
        jpaRepository.deleteByThreadId(threadId)
}
