package com.example.chatbot.application.chat.port.out

import com.example.chatbot.domain.chat.Chat

interface ChatRepository {
    fun save(chat: Chat): Chat
    fun findById(id: String): Chat?
    fun findByThreadId(threadId: String): List<Chat>
    fun findByUserId(userId: String): List<Chat>
    fun findAll(): List<Chat>
    fun findByCreatedAtAfter(threshold: java.time.Instant): List<Chat>
    fun deleteByThreadId(threadId: String)
}
