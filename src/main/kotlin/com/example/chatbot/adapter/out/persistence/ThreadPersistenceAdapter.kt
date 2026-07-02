package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.application.chat.port.out.ThreadRepository
import com.example.chatbot.application.common.PageResult
import com.example.chatbot.domain.chat.ChatThread
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ThreadPersistenceAdapter(
    private val jpaRepository: ThreadJpaRepository,
) : ThreadRepository {

    override fun save(thread: ChatThread): ChatThread =
        jpaRepository.save(ThreadJpaEntity.fromDomain(thread)).toDomain()

    override fun findById(id: String): ChatThread? =
        jpaRepository.findByIdOrNull(id)?.toDomain()

    override fun findLatestByUserId(userId: String): ChatThread? =
        jpaRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId)?.toDomain()

    override fun findByUserId(userId: String): List<ChatThread> =
        jpaRepository.findByUserId(userId).map { it.toDomain() }

    override fun findAll(): List<ChatThread> =
        jpaRepository.findAll().map { it.toDomain() }

    override fun findPage(userId: String?, ascending: Boolean, page: Int, size: Int): PageResult<ChatThread> {
        val direction = if (ascending) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"))
        val result = if (userId == null) jpaRepository.findAll(pageable) else jpaRepository.findByUserId(userId, pageable)
        return PageResult(result.content.map { it.toDomain() }, page, size, result.totalElements)
    }

    override fun deleteById(id: String) =
        jpaRepository.deleteById(id)
}
