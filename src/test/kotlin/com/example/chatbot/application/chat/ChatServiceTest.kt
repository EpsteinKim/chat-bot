package com.example.chatbot.application.chat

import com.example.chatbot.application.chat.port.`in`.CreateChatCommand
import com.example.chatbot.application.chat.port.`in`.CreateChatResult
import com.example.chatbot.application.chat.port.`in`.ListThreadsQuery
import com.example.chatbot.adapter.out.llm.MockLlmAdapter
import com.example.chatbot.domain.chat.ChatThread
import com.example.chatbot.support.InMemoryActivityRecorder
import com.example.chatbot.support.InMemoryChatRepository
import com.example.chatbot.support.InMemoryThreadRepository
import com.example.chatbot.support.NoopTransactionManager
import kotlinx.coroutines.runBlocking
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ChatServiceTest {

    private val now = Instant.parse("2026-07-02T00:00:00Z")
    private val clock = Clock.fixed(now, ZoneOffset.UTC)

    private val chatRepository = InMemoryChatRepository()
    private val threadRepository = InMemoryThreadRepository()
    private val llm = MockLlmAdapter("gpt-test")
    private val policy = ChatPolicy(idleMinutes = 30)
    private val activityRecorder = InMemoryActivityRecorder()

    private val service =
        ChatService(llm, chatRepository, threadRepository, policy, activityRecorder, NoopTransactionManager(), clock)

    private fun create(command: CreateChatCommand): CreateChatResult = runBlocking { service.create(command) }

    @Test
    fun `대화를 저장하고 답변을 반환한다`() {
        val result = create(CreateChatCommand(userId = "u1", question = "hello"))

        assertTrue(result.answer.isNotBlank())
        assertEquals("hello", result.question)
        assertEquals(1, chatRepository.findByUserId("u1").size)
        assertEquals(1, threadRepository.findByUserId("u1").size)
        assertEquals(result.threadId, threadRepository.findByUserId("u1").single().id)
    }

    @Test
    fun `유휴 시간 이내의 두 번째 질문은 기존 스레드를 재사용한다`() {
        val first = create(CreateChatCommand("u1", "first"))
        val second = create(CreateChatCommand("u1", "second"))

        assertEquals(first.threadId, second.threadId)
        assertEquals(1, threadRepository.findByUserId("u1").size)
        assertEquals(2, chatRepository.findByThreadId(first.threadId).size)
    }

    @Test
    fun `유휴 시간이 지난 뒤의 질문은 새 스레드를 생성한다`() {
        val stale = ChatThread(
            id = "stale",
            userId = "u1",
            createdAt = now.minusSeconds(60 * 60),
            updatedAt = now.minusSeconds(31 * 60),
        )
        threadRepository.save(stale)

        val result = create(CreateChatCommand("u1", "new question"))

        assertNotEquals("stale", result.threadId)
        assertEquals(2, threadRepository.findByUserId("u1").size)
    }

    @Test
    fun `일반 회원은 자신의 스레드만 관리자는 모든 스레드를 조회한다`() {
        create(CreateChatCommand("alice", "q"))
        create(CreateChatCommand("bob", "q"))

        val aliceView = service.listThreads(ListThreadsQuery(userId = "alice", isAdmin = false))
        assertEquals(1, aliceView.items.size)
        assertEquals("alice", aliceView.items.single().userId)

        val adminView = service.listThreads(ListThreadsQuery(userId = "alice", isAdmin = true))
        assertEquals(2, adminView.items.size)
    }

    @Test
    fun `소유자는 자신의 스레드와 대화를 삭제할 수 있다`() {
        val result = create(CreateChatCommand("u1", "q"))

        service.deleteThread(userId = "u1", isAdmin = false, threadId = result.threadId)

        assertTrue(threadRepository.findByUserId("u1").isEmpty())
        assertTrue(chatRepository.findByThreadId(result.threadId).isEmpty())
    }

    @Test
    fun `타인의 스레드 삭제는 거부된다`() {
        val result = create(CreateChatCommand("owner", "q"))

        assertFailsWith<ThreadAccessDeniedException> {
            service.deleteThread(userId = "intruder", isAdmin = false, threadId = result.threadId)
        }
        assertEquals(1, threadRepository.findByUserId("owner").size)
    }

    @Test
    fun `관리자는 모든 스레드를 삭제할 수 있다`() {
        val result = create(CreateChatCommand("owner", "q"))

        service.deleteThread(userId = "admin", isAdmin = true, threadId = result.threadId)

        assertTrue(threadRepository.findByUserId("owner").isEmpty())
    }

    @Test
    fun `스트림은 청크를 방출하고 조립된 답변을 저장한다`() {
        val chunks = service.stream(CreateChatCommand("u1", "stream please")).collectList().block()

        assertTrue(!chunks.isNullOrEmpty())
        assertEquals(1, chatRepository.findByUserId("u1").size)
        assertEquals(chunks!!.joinToString(""), chatRepository.findByUserId("u1").single().answer)
    }
}
