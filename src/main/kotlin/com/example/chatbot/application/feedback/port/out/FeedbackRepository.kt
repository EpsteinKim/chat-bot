package com.example.chatbot.application.feedback.port.out

import com.example.chatbot.application.common.PageResult
import com.example.chatbot.domain.feedback.Feedback

interface FeedbackRepository {
    fun save(feedback: Feedback): Feedback
    fun findById(id: String): Feedback?
    fun existsByUserIdAndChatId(userId: String, chatId: String): Boolean
    fun findPage(userId: String?, positive: Boolean?, ascending: Boolean, page: Int, size: Int): PageResult<Feedback>
}
