package com.example.chatbot.security

import com.example.chatbot.application.user.port.out.TokenBundle
import com.example.chatbot.application.user.port.out.TokenClaims
import com.example.chatbot.application.user.port.out.TokenProvider
import com.example.chatbot.config.JwtProperties
import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val properties: JwtProperties,
    private val clock: Clock,
) : TokenProvider {

    private val key: SecretKey = Keys.hmacShaKeyFor(properties.secret.toByteArray())
    private val jwtClock = io.jsonwebtoken.Clock { Date.from(Instant.now(clock)) }

    override fun issue(user: User): TokenBundle {
        val now = Instant.now(clock)
        val expiresAt = now.plusMillis(properties.expirationMs)
        val token = Jwts.builder()
            .subject(user.id)
            .claim("email", user.email)
            .claim("role", user.role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(key)
            .compact()
        return TokenBundle(token, TOKEN_TYPE, properties.expirationMs, expiresAt)
    }

    override fun parse(token: String): TokenClaims {
        val claims = Jwts.parser()
            .verifyWith(key)
            .clock(jwtClock)
            .build()
            .parseSignedClaims(token)
            .payload
        return TokenClaims(
            userId = claims.subject,
            email = claims["email", String::class.java],
            role = Role.valueOf(claims["role", String::class.java]),
            expiresAt = claims.expiration.toInstant(),
        )
    }

    companion object {
        const val TOKEN_TYPE = "Bearer"
    }
}
