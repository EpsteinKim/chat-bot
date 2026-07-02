package com.example.chatbot.adapter.out.security

import com.example.chatbot.application.user.port.out.PasswordHasher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordHasher : PasswordHasher {
    private val encoder = BCryptPasswordEncoder()

    override fun hash(rawPassword: String): String = encoder.encode(rawPassword)
    
    override fun matches(rawPassword: String, hash: String): Boolean =
        encoder.matches(rawPassword, hash)
}
