package com.example.chatbot.application.chat.port.out

import com.example.chatbot.application.common.PageResult
import com.example.chatbot.domain.chat.ChatThread

interface ThreadRepository {
    fun save(thread: ChatThread): ChatThread
    fun findById(id: String): ChatThread?
    fun findLatestByUserId(userId: String): ChatThread?
    fun findByUserId(userId: String): List<ChatThread>
    fun findAll(): List<ChatThread>
    fun findPage(userId: String?, ascending: Boolean, page: Int, size: Int): PageResult<ChatThread>
    fun deleteById(id: String)
}
