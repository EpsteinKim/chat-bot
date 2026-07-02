package com.example.chatbot.adapter.`in`.web

import com.example.chatbot.adapter.`in`.web.dto.CreateFeedbackRequest
import com.example.chatbot.adapter.`in`.web.dto.FeedbackPageResponse
import com.example.chatbot.adapter.`in`.web.dto.FeedbackResponse
import com.example.chatbot.adapter.`in`.web.dto.UpdateFeedbackStatusRequest
import com.example.chatbot.application.feedback.port.`in`.CreateFeedbackCommand
import com.example.chatbot.application.feedback.port.`in`.CreateFeedbackUseCase
import com.example.chatbot.application.feedback.port.`in`.ListFeedbackQuery
import com.example.chatbot.application.feedback.port.`in`.QueryFeedbackUseCase
import com.example.chatbot.application.feedback.port.`in`.UpdateFeedbackStatusCommand
import com.example.chatbot.application.feedback.port.`in`.UpdateFeedbackStatusUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController(
    private val createFeedbackUseCase: CreateFeedbackUseCase,
    private val queryFeedbackUseCase: QueryFeedbackUseCase,
    private val updateFeedbackStatusUseCase: UpdateFeedbackStatusUseCase,
    private val currentUser: CurrentUser,
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateFeedbackRequest,
    ): ResponseEntity<FeedbackResponse> {
        val user = currentUser.resolve()
        val result = createFeedbackUseCase.create(CreateFeedbackCommand(user.userId, user.isAdmin, request.chatId, request.positive))
        return ResponseEntity.status(201).body(FeedbackResponse.from(result))
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) isPositive: Boolean?,
        @RequestParam(defaultValue = "false") ascending: Boolean,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): FeedbackPageResponse {
        val user = currentUser.resolve()
        val query = ListFeedbackQuery(user.userId, user.isAdmin, isPositive, ascending, page, size)
        return FeedbackPageResponse.from(queryFeedbackUseCase.list(query))
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateFeedbackStatusRequest,
    ): FeedbackResponse {
        val user = currentUser.resolve()
        val result = updateFeedbackStatusUseCase.updateStatus(UpdateFeedbackStatusCommand(user.isAdmin, id, request.status))
        return FeedbackResponse.from(result)
    }
}
