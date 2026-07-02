package com.example.chatbot.adapter.out.persistence

import com.example.chatbot.application.user.port.out.UserRepository
import com.example.chatbot.domain.user.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val jpaRepository: UserJpaRepository,
) : UserRepository {

    override fun save(user: User): User =
        jpaRepository.save(UserJpaEntity.fromDomain(user)).toDomain()

    override fun findByEmail(email: String): User? =
        jpaRepository.findByEmail(email)?.toDomain()

    override fun findById(id: String): User? =
        jpaRepository.findByIdOrNull(id)?.toDomain()

    override fun existsByEmail(email: String): Boolean =
        jpaRepository.existsByEmail(email)
}
