package com.example.chatbot.domain.chat

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChatThreadTest {

    private val base = Instant.parse("2026-07-02T00:00:00Z")

    private fun thread(updatedAt: Instant) =
        ChatThread(id = "t1", userId = "u1", createdAt = base, updatedAt = updatedAt)

    @Test
    fun `유휴 시간 이내면 활성 상태다`() {
        val t = thread(base)
        assertTrue(t.isActiveAt(base.plusSeconds(29 * 60), idleMinutes = 30))
    }

    @Test
    fun `유휴 시간에 도달하면 비활성 상태다`() {
        val t = thread(base)
        assertFalse(t.isActiveAt(base.plusSeconds(30 * 60), idleMinutes = 30))
    }

    @Test
    fun `touched는 수정시각만 갱신한다`() {
        val t = thread(base)
        val later = base.plusSeconds(600)
        val touched = t.touched(later)
        assertEquals(later, touched.updatedAt)
        assertEquals(t.id, touched.id)
        assertEquals(t.createdAt, touched.createdAt)
    }
}
