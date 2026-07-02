package com.example.chatbot.support

import com.example.chatbot.application.chat.port.out.ThreadRepository
import com.example.chatbot.application.common.PageResult
import com.example.chatbot.domain.chat.ChatThread

class InMemoryThreadRepository : ThreadRepository {
    private val store = LinkedHashMap<String, ChatThread>()

    override fun save(thread: ChatThread): ChatThread {
        store[thread.id] = thread
        return thread
    }

    override fun findById(id: String): ChatThread? = store[id]

    override fun findLatestByUserId(userId: String): ChatThread? =
        store.values.filter { it.userId == userId }.maxByOrNull { it.updatedAt }

    override fun findByUserId(userId: String): List<ChatThread> =
        store.values.filter { it.userId == userId }

    override fun findAll(): List<ChatThread> = store.values.toList()

    override fun findPage(userId: String?, ascending: Boolean, page: Int, size: Int): PageResult<ChatThread> {
        val filtered = store.values.filter { userId == null || it.userId == userId }
        val ordered = filtered.sortedBy { it.createdAt }.let { if (ascending) it else it.reversed() }
        val items = ordered.drop(page * size).take(size)
        return PageResult(items, page, size, filtered.size.toLong())
    }

    override fun deleteById(id: String) {
        store.remove(id)
    }
}
