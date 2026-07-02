package com.example.chatbot.application.activity.port.`in`

interface ReportUseCase {
    fun generateCsv(isAdmin: Boolean): ReportResult
}

data class ReportResult(
    val filename: String,
    val csv: String,
)
