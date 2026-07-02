package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.domain.user.Role
import com.example.chatbot.domain.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: Role,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
) {
    fun toDomain(): User = User(id, email, passwordHash, name, role, createdAt)

    companion object {
        fun fromDomain(user: User): UserJpaEntity =
            UserJpaEntity(user.id, user.email, user.passwordHash, user.name, user.role, user.createdAt)
    }
}
