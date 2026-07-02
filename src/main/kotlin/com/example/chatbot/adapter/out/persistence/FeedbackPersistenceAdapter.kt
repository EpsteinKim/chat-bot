package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.application.common.PageResult
import com.example.chatbot.application.feedback.port.out.FeedbackRepository
import com.example.chatbot.domain.feedback.Feedback
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class FeedbackPersistenceAdapter(
    private val jpaRepository: FeedbackJpaRepository,
) : FeedbackRepository {

    override fun save(feedback: Feedback): Feedback =
        jpaRepository.save(FeedbackJpaEntity.fromDomain(feedback)).toDomain()

    override fun findById(id: String): Feedback? =
        jpaRepository.findById(id).map { it.toDomain() }.orElse(null)

    override fun existsByUserIdAndChatId(userId: String, chatId: String): Boolean =
        jpaRepository.existsByUserIdAndChatId(userId, chatId)

    override fun findPage(userId: String?, positive: Boolean?, ascending: Boolean, page: Int, size: Int): PageResult<Feedback> {
        val direction = if (ascending) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"))
        val result = when {
            userId == null && positive == null -> jpaRepository.findAll(pageable)
            userId == null -> jpaRepository.findByIsPositive(positive!!, pageable)
            positive == null -> jpaRepository.findByUserId(userId, pageable)
            else -> jpaRepository.findByUserIdAndIsPositive(userId, positive, pageable)
        }
        return PageResult(result.content.map { it.toDomain() }, page, size, result.totalElements)
    }
}
