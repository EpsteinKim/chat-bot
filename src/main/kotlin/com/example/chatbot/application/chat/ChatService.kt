package com.example.chatbot.application.chat

import com.example.chatbot.application.activity.port.`in`.RecordActivityUseCase
import com.example.chatbot.application.common.PageResult
import com.example.chatbot.application.chat.port.`in`.CreateChatCommand
import com.example.chatbot.application.chat.port.`in`.CreateChatResult
import com.example.chatbot.application.chat.port.`in`.CreateChatUseCase
import com.example.chatbot.application.chat.port.`in`.ListThreadsQuery
import com.example.chatbot.application.chat.port.`in`.QueryChatUseCase
import com.example.chatbot.application.chat.port.`in`.ThreadView
import com.example.chatbot.application.chat.port.out.ChatRepository
import com.example.chatbot.application.chat.port.out.LlmPort
import com.example.chatbot.application.chat.port.out.LlmPrompt
import com.example.chatbot.application.chat.port.out.LlmTurn
import com.example.chatbot.application.chat.port.out.ThreadRepository
import com.example.chatbot.domain.activity.ActivityType
import com.example.chatbot.domain.chat.Chat
import com.example.chatbot.domain.chat.ChatThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class ChatService(
    private val llmPort: LlmPort,
    private val chatRepository: ChatRepository,
    private val threadRepository: ThreadRepository,
    private val chatPolicy: ChatPolicy,
    private val recordActivityUseCase: RecordActivityUseCase,
    transactionManager: PlatformTransactionManager,
    private val clock: Clock,
) : CreateChatUseCase, QueryChatUseCase {

    private val transaction = TransactionTemplate(transactionManager)

    override suspend fun create(command: CreateChatCommand): CreateChatResult {
        val prepared = withContext(Dispatchers.IO) { prepareInTransaction(command) }
        val completion = llmPort.complete(prepared.prompt)
        val chat = withContext(Dispatchers.IO) {
            persistInTransaction(command, prepared.thread, completion.answer, completion.model)
        }
        return CreateChatResult(
            chatId = chat.id,
            threadId = chat.threadId,
            question = chat.question,
            answer = chat.answer,
            model = chat.model,
            createdAt = chat.createdAt,
        )
    }

    override fun stream(command: CreateChatCommand): Flux<String> {
        val prepared = prepareInTransaction(command)
        val collected = StringBuilder()

        return llmPort.stream(prepared.prompt)
            .doOnNext { collected.append(it) }
            .doOnComplete {
                persistInTransaction(
                    command,
                    prepared.thread,
                    collected.toString(),
                    command.model ?: prepared.prompt.model.orEmpty(),
                )
            }
    }

    override fun listThreads(query: ListThreadsQuery): PageResult<ThreadView> {
        val userId = if (query.isAdmin) null else query.userId
        val page = threadRepository.findPage(userId, query.ascending, query.page, query.size)
        val views = page.items.map { thread ->
            val chats = chatRepository.findByThreadId(thread.id).sortedBy { it.createdAt }
            ThreadView(thread.id, thread.userId, chats)
        }
        return PageResult(views, page.page, page.size, page.totalElements)
    }

    override fun deleteThread(userId: String, isAdmin: Boolean, threadId: String) {
        transaction.executeWithoutResult {
            val thread = threadRepository.findById(threadId) ?: return@executeWithoutResult
            chatPolicy.assertCanDeleteThread(userId, isAdmin, thread)
            chatRepository.deleteByThreadId(threadId)
            threadRepository.deleteById(threadId)
        }
    }

    private fun prepareInTransaction(command: CreateChatCommand): PreparedPrompt =
        transaction.execute {
            val now = Instant.now(clock)
            val thread = resolveThread(command.userId, now)
            val prompt = buildPrompt(command, thread)
            PreparedPrompt(thread, prompt)
        }!!

    private fun persistInTransaction(
        command: CreateChatCommand,
        thread: ChatThread,
        answer: String,
        model: String,
    ): Chat =
        transaction.execute {
            val now = Instant.now(clock)
            val chat = Chat(
                id = UUID.randomUUID().toString(),
                threadId = thread.id,
                userId = command.userId,
                question = command.question,
                answer = answer,
                model = model,
                createdAt = now,
            )
            val saved = chatRepository.save(chat)
            threadRepository.save(thread.touched(now))
            recordActivityUseCase.record(command.userId, ActivityType.CHAT_CREATED)
            saved
        }!!

    private fun resolveThread(userId: String, now: Instant): ChatThread {
        val latest = threadRepository.findLatestByUserId(userId)
        return if (latest != null && chatPolicy.canContinue(latest, now)) {
            threadRepository.save(latest.touched(now))
        } else {
            threadRepository.save(ChatThread(UUID.randomUUID().toString(), userId, now, now))
        }
    }

    private fun buildPrompt(command: CreateChatCommand, thread: ChatThread): LlmPrompt {
        val history = chatRepository.findByThreadId(thread.id)
            .sortedBy { it.createdAt }
            .map { LlmTurn(it.question, it.answer) }
        return LlmPrompt(question = command.question, model = command.model, history = history)
    }

    private data class PreparedPrompt(val thread: ChatThread, val prompt: LlmPrompt)
}
