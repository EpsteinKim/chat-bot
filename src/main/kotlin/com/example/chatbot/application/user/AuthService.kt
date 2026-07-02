package com.example.chatbot.application.user

import com.example.chatbot.application.user.port.`in`.LoginCommand
import com.example.chatbot.application.user.port.`in`.LoginResult
import com.example.chatbot.application.user.port.`in`.LoginUseCase
import com.example.chatbot.application.user.port.`in`.LogoutUseCase
import com.example.chatbot.application.user.port.`in`.SignUpCommand
import com.example.chatbot.application.user.port.`in`.SignUpResult
import com.example.chatbot.application.user.port.`in`.SignUpUseCase
import com.example.chatbot.application.activity.port.`in`.RecordActivityUseCase
import com.example.chatbot.application.user.port.out.PasswordHasher
import com.example.chatbot.application.user.port.out.TokenProvider
import com.example.chatbot.application.user.port.out.UserRepository
import com.example.chatbot.domain.activity.ActivityType
import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenProvider: TokenProvider,
    private val recordActivityUseCase: RecordActivityUseCase,
    private val clock: Clock,
) : SignUpUseCase, LoginUseCase, LogoutUseCase {

    @Transactional
    override fun signUp(command: SignUpCommand): SignUpResult {
        if (userRepository.existsByEmail(command.email)) {
            throw EmailAlreadyExistsException(command.email)
        }
        val user = User(
            id = UUID.randomUUID().toString(),
            email = command.email,
            passwordHash = passwordHasher.hash(command.password),
            name = command.name,
            role = Role.MEMBER,
            createdAt = Instant.now(clock),
        )
        val saved = userRepository.save(user)
        recordActivityUseCase.record(saved.id, ActivityType.SIGNUP)
        return SignUpResult(saved.id, saved.email, saved.name, saved.role, saved.createdAt)
    }

    @Transactional
    override fun login(command: LoginCommand): LoginResult {
        val user = userRepository.findByEmail(command.email)
            ?: throw InvalidCredentialsException()
        if (!passwordHasher.matches(command.password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }
        val bundle = tokenProvider.issue(user)
        recordActivityUseCase.record(user.id, ActivityType.LOGIN)
        return LoginResult(bundle.accessToken, bundle.tokenType, bundle.expiresInMs)
    }

    override fun logout(token: String) {
        tokenProvider.parse(token)
    }
}
