package com.example.chatbot.security

import com.example.chatbot.config.JwtProperties
import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class JwtTokenProviderTest {

    private val now = Instant.parse("2026-07-02T00:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)
    private val properties = JwtProperties(
        secret = "unit-test-secret-key-that-is-long-enough-for-hs512-signing-algorithm",
        expirationMs = 3_600_000,
    )
    private val provider = JwtTokenProvider(properties, clock)

    private val user = User(
        id = "u1",
        email = "a@acme.com",
        passwordHash = "irrelevant",
        name = "Alice",
        role = Role.ADMIN,
        createdAt = now,
    )

    @Test
    fun `발급된 토큰은 원래 클레임으로 복원된다`() {
        val bundle = provider.issue(user)
        val claims = provider.parse(bundle.accessToken)

        assertEquals("u1", claims.userId)
        assertEquals("a@acme.com", claims.email)
        assertEquals(Role.ADMIN, claims.role)
    }

    @Test
    fun `만료 시각은 시계 기준으로 발급된다`() {
        val bundle = provider.issue(user)

        assertEquals(now.plusMillis(properties.expirationMs), bundle.expiresAt)
    }

    @Test
    fun `변조된 토큰은 검증에 실패한다`() {
        val bundle = provider.issue(user)
        val tampered = bundle.accessToken.dropLast(4) + "abcd"

        assertFails { provider.parse(tampered) }
    }
}
