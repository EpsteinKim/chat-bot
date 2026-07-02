package com.example.chatbot.application.user

import com.example.chatbot.adapter.out.security.BCryptPasswordHasher
import com.example.chatbot.application.user.port.`in`.LoginCommand
import com.example.chatbot.application.user.port.`in`.SignUpCommand
import com.example.chatbot.config.JwtProperties
import com.example.chatbot.domain.user.Role
import com.example.chatbot.security.JwtTokenProvider
import com.example.chatbot.support.InMemoryActivityRecorder
import com.example.chatbot.support.InMemoryUserRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AuthServiceTest {

    private val now = Instant.parse("2026-07-02T00:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)

    private val userRepository = InMemoryUserRepository()
    private val passwordHasher = BCryptPasswordHasher()
    private val jwtProperties = JwtProperties(
        secret = "unit-test-secret-key-that-is-long-enough-for-hs512-signing-algorithm",
        expirationMs = 3_600_000,
    )
    private val tokenProvider = JwtTokenProvider(jwtProperties, clock)
    private val activityRecorder = InMemoryActivityRecorder()

    private val service = AuthService(userRepository, passwordHasher, tokenProvider, activityRecorder, clock)

    private val email = "test.com"
    private val name = "Epstein"
    private val password = "asdfasfdsafsa"
    @Test
    fun `회원가입은 해시된 비밀번호로 회원을 저장한다`() {
        val result = service.signUp(SignUpCommand(email, password, name))

        assertEquals(Role.MEMBER, result.role)
        assertEquals(now, result.createdAt)

        val stored = userRepository.findByEmail(email)!!
        assertNotEquals(password, stored.passwordHash)
        assertTrue(passwordHasher.matches(password, stored.passwordHash))
    }

    @Test
    fun `중복 이메일 회원가입은 거부된다`() {
        service.signUp(SignUpCommand(email, password, name))

        assertFailsWith<EmailAlreadyExistsException> {
            service.signUp(SignUpCommand(email, password, name + "wrong"))
        }
    }

    @Test
    fun `로그인은 사용자 클레임을 담은 토큰을 반환한다`() {
        service.signUp(SignUpCommand(email, password, name))

        val result = service.login(LoginCommand(email, password))

        assertEquals("Bearer", result.tokenType)
        val claims = tokenProvider.parse(result.accessToken)
        assertEquals(email, claims.email)
        assertEquals(Role.MEMBER, claims.role)
    }

    @Test
    fun `잘못된 비밀번호 로그인은 거부된다`() {
        service.signUp(SignUpCommand(email, password, name))

        assertFailsWith<InvalidCredentialsException> {
            service.login(LoginCommand(email, password + "wrong"))
        }
    }

    @Test
    fun `존재하지 않는 이메일 로그인은 거부된다`() {
        assertFailsWith<InvalidCredentialsException> {
            service.login(LoginCommand("unknown-$email", password))
        }
    }

    @Test
    fun `로그아웃은 유효한 토큰을 파싱하며 예외 없이 완료된다`() {
        service.signUp(SignUpCommand(email, password, name))
        val token = service.login(LoginCommand(email, password)).accessToken

        service.logout(token)
    }
}
