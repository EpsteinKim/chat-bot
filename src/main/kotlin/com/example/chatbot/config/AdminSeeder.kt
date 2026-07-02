package com.example.chatbot.config

import com.example.chatbot.application.user.port.out.PasswordHasher
import com.example.chatbot.application.user.port.out.UserRepository
import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Component
class AdminSeeder(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val adminProperties: AdminProperties,
    private val clock: Clock,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(AdminSeeder::class.java)

    override fun run(args: ApplicationArguments) {
        if (userRepository.existsByEmail(adminProperties.email)) {
            return
        }
        val admin = User(
            id = UUID.randomUUID().toString(),
            email = adminProperties.email,
            passwordHash = passwordHasher.hash(adminProperties.password),
            name = adminProperties.name,
            role = Role.ADMIN,
            createdAt = Instant.now(clock),
        )
        userRepository.save(admin)
        log.info("Seeded admin account: {}", adminProperties.email)
    }
}
