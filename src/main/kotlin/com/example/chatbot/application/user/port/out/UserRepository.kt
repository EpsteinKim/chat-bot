package com.example.chatbot.application.user.port.out

import com.example.chatbot.domain.user.User

interface UserRepository {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun findById(id: String): User?
    fun existsByEmail(email: String): Boolean
}
