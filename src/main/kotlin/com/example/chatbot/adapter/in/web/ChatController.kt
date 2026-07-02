package com.example.chatbot.adapter.`in`.web

import com.example.chatbot.adapter.`in`.web.dto.CreateChatRequest
import com.example.chatbot.adapter.`in`.web.dto.CreateChatResponse
import com.example.chatbot.adapter.`in`.web.dto.ThreadPageResponse
import com.example.chatbot.application.chat.port.`in`.CreateChatCommand
import com.example.chatbot.application.chat.port.`in`.CreateChatUseCase
import com.example.chatbot.application.chat.port.`in`.ListThreadsQuery
import com.example.chatbot.application.chat.port.`in`.QueryChatUseCase
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val createChatUseCase: CreateChatUseCase,
    private val queryChatUseCase: QueryChatUseCase,
    private val currentUser: CurrentUser,
) {
    private val log = LoggerFactory.getLogger(ChatController::class.java)

    @PostMapping
    suspend fun create(
        @Valid @RequestBody request: CreateChatRequest,
    ): Any {
        val user = currentUser.resolve()
        val command = CreateChatCommand(user.userId, request.question, request.model)
        return if (request.isStreaming) stream(command) else {
            val result = createChatUseCase.create(command)
            ResponseEntity.ok(CreateChatResponse.from(result))
        }
    }

    @GetMapping
    fun list(
        @RequestParam(defaultValue = "false") ascending: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ThreadPageResponse {
        val user = currentUser.resolve()
        val query = ListThreadsQuery(user.userId, user.isAdmin, ascending, page, size)
        return ThreadPageResponse.from(queryChatUseCase.listThreads(query))
    }

    @DeleteMapping("/threads/{threadId}")
    fun deleteThread(
        @PathVariable threadId: String,
    ): ResponseEntity<Void> {
        val user = currentUser.resolve()
        queryChatUseCase.deleteThread(user.userId, user.isAdmin, threadId)
        return ResponseEntity.noContent().build()
    }

    private fun stream(command: CreateChatCommand): SseEmitter {
        val emitter = SseEmitter(STREAM_TIMEOUT_MS)
        createChatUseCase.stream(command).subscribe(
            { chunk -> emitter.send(SseEmitter.event().data(chunk, MediaType.TEXT_PLAIN)) },
            { error ->
                log.error("stream failed", error)
                emitter.completeWithError(error)
            },
            { emitter.complete() },
        )
        return emitter
    }

    companion object {
        const val STREAM_TIMEOUT_MS = 120_000L
    }
}
