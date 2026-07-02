package com.example.chatbot.adapter.`in`.web

import com.example.chatbot.adapter.`in`.web.dto.LoginRequest
import com.example.chatbot.adapter.`in`.web.dto.LoginResponse
import com.example.chatbot.adapter.`in`.web.dto.SignUpRequest
import com.example.chatbot.adapter.`in`.web.dto.SignUpResponse
import com.example.chatbot.application.user.port.`in`.LoginCommand
import com.example.chatbot.application.user.port.`in`.LoginUseCase
import com.example.chatbot.application.user.port.`in`.LogoutUseCase
import com.example.chatbot.application.user.port.`in`.SignUpCommand
import com.example.chatbot.application.user.port.`in`.SignUpUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
) {

    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: SignUpRequest): ResponseEntity<SignUpResponse> {
        val result = signUpUseCase.signUp(SignUpCommand(request.email, request.password, request.name))
        return ResponseEntity.status(HttpStatus.CREATED).body(SignUpResponse.from(result))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val result = loginUseCase.login(LoginCommand(request.email, request.password))
        return ResponseEntity.ok(LoginResponse.from(result))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String): ResponseEntity<Void> {
        val token = authorization.removePrefix(BEARER_PREFIX).trim()
        logoutUseCase.logout(token)
        return ResponseEntity.noContent().build()
    }

    companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}
