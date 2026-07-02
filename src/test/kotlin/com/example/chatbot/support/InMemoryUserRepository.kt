package com.example.chatbot.support

import com.example.chatbot.application.user.port.out.UserRepository
import com.example.chatbot.domain.user.User

class InMemoryUserRepository : UserRepository {
    private val store = LinkedHashMap<String, User>()

    override fun save(user: User): User {
        store[user.id] = user
        return user
    }

    override fun findByEmail(email: String): User? =
        store.values.firstOrNull { it.email == email }

    override fun findById(id: String): User? = store[id]

    override fun existsByEmail(email: String): Boolean =
        store.values.any { it.email == email }
}
