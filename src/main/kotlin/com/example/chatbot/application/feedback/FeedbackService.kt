package com.example.chatbot.application.feedback

import com.example.chatbot.application.chat.port.out.ChatRepository
import com.example.chatbot.application.common.AccessDeniedException
import com.example.chatbot.application.common.PageResult
import com.example.chatbot.application.feedback.port.`in`.CreateFeedbackCommand
import com.example.chatbot.application.feedback.port.`in`.CreateFeedbackUseCase
import com.example.chatbot.application.feedback.port.`in`.FeedbackResult
import com.example.chatbot.application.feedback.port.`in`.ListFeedbackQuery
import com.example.chatbot.application.feedback.port.`in`.QueryFeedbackUseCase
import com.example.chatbot.application.feedback.port.`in`.UpdateFeedbackStatusCommand
import com.example.chatbot.application.feedback.port.`in`.UpdateFeedbackStatusUseCase
import com.example.chatbot.application.feedback.port.out.FeedbackRepository
import com.example.chatbot.domain.feedback.Feedback
import com.example.chatbot.domain.feedback.FeedbackStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val chatRepository: ChatRepository,
    private val clock: Clock,
) : CreateFeedbackUseCase, QueryFeedbackUseCase, UpdateFeedbackStatusUseCase {

    @Transactional
    override fun create(command: CreateFeedbackCommand): FeedbackResult {
        val chat = chatRepository.findById(command.chatId) ?: throw ChatNotFoundException(command.chatId)
        if (!command.isAdmin && chat.userId != command.userId) {
            throw AccessDeniedException("cannot create feedback on another user's chat")
        }
        if (feedbackRepository.existsByUserIdAndChatId(command.userId, command.chatId)) {
            throw DuplicateFeedbackException(command.chatId)
        }
        val feedback = Feedback(
            id = UUID.randomUUID().toString(),
            userId = command.userId,
            chatId = command.chatId,
            positive = command.positive,
            status = FeedbackStatus.PENDING,
            createdAt = Instant.now(clock),
        )
        return toResult(feedbackRepository.save(feedback))
    }

    @Transactional(readOnly = true)
    override fun list(query: ListFeedbackQuery): PageResult<FeedbackResult> {
        val userId = if (query.isAdmin) null else query.userId
        val page = feedbackRepository.findPage(userId, query.positive, query.ascending, query.page, query.size)
        return PageResult(page.items.map { toResult(it) }, page.page, page.size, page.totalElements)
    }

    @Transactional
    override fun updateStatus(command: UpdateFeedbackStatusCommand): FeedbackResult {
        if (!command.isAdmin) throw AccessDeniedException("only admin can update feedback status")
        val feedback = feedbackRepository.findById(command.feedbackId) ?: throw FeedbackNotFoundException(command.feedbackId)
        return toResult(feedbackRepository.save(feedback.withStatus(command.status)))
    }

    private fun toResult(feedback: Feedback): FeedbackResult =
        FeedbackResult(
            id = feedback.id,
            userId = feedback.userId,
            chatId = feedback.chatId,
            positive = feedback.positive,
            status = feedback.status,
            createdAt = feedback.createdAt,
        )
}
