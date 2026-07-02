package com.example.chatbot.application.chat

import com.example.chatbot.domain.chat.ChatThread
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ChatPolicy(
    @Value("\${app.chat.thread-idle-minutes:30}") private val idleMinutes: Long,
) {
    fun canContinue(thread: ChatThread, now: Instant): Boolean = thread.isActiveAt(now, idleMinutes)

    fun assertCanDeleteThread(userId: String, isAdmin: Boolean, thread: ChatThread) {
        if (!isAdmin && thread.userId != userId) {
            throw ThreadAccessDeniedException(thread.id)
        }
    }
}

class ThreadAccessDeniedException(threadId: String) :
    RuntimeException("thread $threadId is not owned by the requester")
