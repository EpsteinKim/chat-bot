package com.example.chatbot.application.common

data class PageResult<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
) {
    val totalPages: Int get() = if (size <= 0) 0 else ((totalElements + size - 1) / size).toInt()
}
