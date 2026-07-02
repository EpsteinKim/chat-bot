package com.example.chatbot.support

import com.example.chatbot.application.chat.port.out.ChatRepository
import com.example.chatbot.domain.chat.Chat
import java.time.Instant

class InMemoryChatRepository : ChatRepository {
    private val store = LinkedHashMap<String, Chat>()

    override fun save(chat: Chat): Chat {
        store[chat.id] = chat
        return chat
    }

    override fun findById(id: String): Chat? = store[id]

    override fun findByThreadId(threadId: String): List<Chat> =
        store.values.filter { it.threadId == threadId }

    override fun findByUserId(userId: String): List<Chat> =
        store.values.filter { it.userId == userId }

    override fun findAll(): List<Chat> = store.values.toList()

    override fun findByCreatedAtAfter(threshold: Instant): List<Chat> =
        store.values.filter { it.createdAt.isAfter(threshold) }

    override fun deleteByThreadId(threadId: String) {
        store.values.removeIf { it.threadId == threadId }
    }
}
