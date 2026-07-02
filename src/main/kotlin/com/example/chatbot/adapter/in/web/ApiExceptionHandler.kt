package com.example.chatbot.adapter.`in`.web

import com.example.chatbot.application.chat.ThreadAccessDeniedException
import com.example.chatbot.application.common.AccessDeniedException
import com.example.chatbot.application.feedback.ChatNotFoundException
import com.example.chatbot.application.feedback.DuplicateFeedbackException
import com.example.chatbot.application.feedback.FeedbackNotFoundException
import com.example.chatbot.application.user.EmailAlreadyExistsException
import com.example.chatbot.application.user.InvalidCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

data class ApiError(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now(),
)

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val message = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError(400, message))
    }

    @ExceptionHandler(ThreadAccessDeniedException::class)
    fun handleAccessDenied(ex: ThreadAccessDeniedException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError(403, ex.message ?: "access denied"))

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailExists(ex: EmailAlreadyExistsException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError(409, ex.message ?: "email already exists"))

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiError(401, ex.message ?: "invalid credentials"))

    @ExceptionHandler(AccessDeniedException::class)
    fun handleCommonAccessDenied(ex: AccessDeniedException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiError(403, ex.message ?: "access denied"))

    @ExceptionHandler(ChatNotFoundException::class)
    fun handleChatNotFound(ex: ChatNotFoundException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError(404, ex.message ?: "chat not found"))

    @ExceptionHandler(FeedbackNotFoundException::class)
    fun handleFeedbackNotFound(ex: FeedbackNotFoundException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError(404, ex.message ?: "feedback not found"))

    @ExceptionHandler(DuplicateFeedbackException::class)
    fun handleDuplicateFeedback(ex: DuplicateFeedbackException): ResponseEntity<ApiError> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError(409, ex.message ?: "duplicate feedback"))
}
