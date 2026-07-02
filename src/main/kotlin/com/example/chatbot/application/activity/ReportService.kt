package com.example.chatbot.application.activity

import com.example.chatbot.application.activity.port.`in`.ReportResult
import com.example.chatbot.application.activity.port.`in`.ReportUseCase
import com.example.chatbot.application.chat.port.out.ChatRepository
import com.example.chatbot.application.common.AccessDeniedException
import com.example.chatbot.application.user.port.out.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Service
class ReportService(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val clock: Clock,
) : ReportUseCase {

    @Transactional(readOnly = true)
    override fun generateCsv(isAdmin: Boolean): ReportResult {
        if (!isAdmin) throw AccessDeniedException("only admin can generate report")
        val from = Instant.now(clock).minus(Duration.ofHours(24))
        val chats = chatRepository.findByCreatedAtAfter(from).sortedBy { it.createdAt }
        val userCache = chats.map { it.userId }.distinct()
            .associateWith { userRepository.findById(it) }
        val header = "chatId,userId,userEmail,userName,question,answer,model,createdAt"
        val rows = chats.map { chat ->
            val user = userCache[chat.userId]
            listOf(
                escape(chat.id),
                escape(chat.userId),
                escape(user?.email),
                escape(user?.name),
                escape(chat.question),
                escape(chat.answer),
                escape(chat.model),
                escape(chat.createdAt.toString()),
            ).joinToString(",")
        }
        val csv = (listOf(header) + rows).joinToString("\n")
        return ReportResult("chat-report.csv", csv)
    }

    private fun escape(value: String?): String {
        if (value == null) return ""
        return if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }
}
