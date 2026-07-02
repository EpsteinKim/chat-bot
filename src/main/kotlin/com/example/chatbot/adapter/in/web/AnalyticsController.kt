package com.example.chatbot.adapter.`in`.web

import com.example.chatbot.adapter.`in`.web.dto.ActivityStatsResponse
import com.example.chatbot.application.activity.port.`in`.ActivityStatsUseCase
import com.example.chatbot.application.activity.port.`in`.ReportUseCase
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val activityStatsUseCase: ActivityStatsUseCase,
    private val reportUseCase: ReportUseCase,
    private val currentUser: CurrentUser,
) {
    @GetMapping("/activity")
    fun activity(): ActivityStatsResponse {
        val user = currentUser.resolve()
        return ActivityStatsResponse.from(activityStatsUseCase.stats(user.isAdmin))
    }

    @GetMapping("/report")
    fun report(): ResponseEntity<String> {
        val user = currentUser.resolve()
        val r = reportUseCase.generateCsv(user.isAdmin)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${r.filename}\"")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(r.csv)
    }
}
