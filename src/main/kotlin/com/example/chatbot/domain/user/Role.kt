package com.example.chatbot.domain.user

enum class Role {
    MEMBER,
    ADMIN,
    ;

    val authority: String get() = "ROLE_$name"
}
