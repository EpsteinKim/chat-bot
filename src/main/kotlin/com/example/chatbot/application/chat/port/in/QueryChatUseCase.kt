package com.example.chatbot.application.chat.port.`in`

import com.example.chatbot.application.common.PageResult
import com.example.chatbot.domain.chat.Chat

interface QueryChatUseCase {
    fun listThreads(query: ListThreadsQuery): PageResult<ThreadView>
    fun deleteThread(userId: String, isAdmin: Boolean, threadId: String)
}

data class ListThreadsQuery(
    val userId: String,
    val isAdmin: Boolean,
    val ascending: Boolean = false,
    val page: Int = 0,
    val size: Int = 20,
)

data class ThreadView(
    val threadId: String,
    val userId: String,
    val chats: List<Chat>,
)
