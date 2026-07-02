package com.example.chatbot.integration

import com.example.chatbot.application.chat.port.out.ChatRepository
import com.example.chatbot.application.chat.port.out.ThreadRepository
import com.example.chatbot.application.user.port.out.UserRepository
import com.example.chatbot.domain.chat.Chat
import com.example.chatbot.domain.chat.ChatThread
import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@SpringBootTest
@Testcontainers
class PersistenceIntegrationTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var threadRepository: ThreadRepository

    @Autowired
    lateinit var chatRepository: ChatRepository

    private val now: Instant = Instant.parse("2026-07-02T00:00:00Z").truncatedTo(ChronoUnit.MICROS)

    private fun uniqueEmail() = "user-${UUID.randomUUID()}@acme.com"

    @Test
    fun `사용자를 실제 Postgres 테이블에 저장하고 이메일로 조회한다`() {
        val email = uniqueEmail()
        val user = User(UUID.randomUUID().toString(), email, "hash", "Alice", Role.MEMBER, now)

        userRepository.save(user)

        val found = userRepository.findByEmail(email)!!
        assertEquals(email, found.email)
        assertEquals(Role.MEMBER, found.role)
        assertEquals(now, found.createdAt)
        assertTrue(userRepository.existsByEmail(email))
    }

    @Test
    fun `이메일 유니크 제약이 실제 DB에서 적용된다`() {
        val email = uniqueEmail()
        userRepository.save(User(UUID.randomUUID().toString(), email, "hash", "Alice", Role.MEMBER, now))

        assertFailsWith<DataIntegrityViolationException> {
            userRepository.save(User(UUID.randomUUID().toString(), email, "hash", "Bob", Role.MEMBER, now))
        }
    }

    @Test
    fun `최신 스레드 조회가 updatedAt 기준으로 동작한다`() {
        val userId = "user-${UUID.randomUUID()}"
        threadRepository.save(ChatThread(UUID.randomUUID().toString(), userId, now, now.minusSeconds(600)))
        val latest = ChatThread(UUID.randomUUID().toString(), userId, now, now)
        threadRepository.save(latest)

        val found = threadRepository.findLatestByUserId(userId)!!
        assertEquals(latest.id, found.id)
    }

    @Test
    fun `대화를 저장하고 스레드로 조회한다`() {
        val threadId = UUID.randomUUID().toString()
        val chat = Chat(UUID.randomUUID().toString(), threadId, "u1", "질문", "답변", "gpt-4o-mini", now)

        chatRepository.save(chat)

        val found = chatRepository.findByThreadId(threadId)
        assertEquals(1, found.size)
        assertEquals("질문", found.single().question)
        assertEquals(now, found.single().createdAt)
    }

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16-alpine")).apply {
            withDatabaseName("chatbot")
            withUsername("chatbot")
            withPassword("chatbot")
        }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.main.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.main.username") { postgres.username }
            registry.add("spring.datasource.main.password") { postgres.password }
        }
    }
}
