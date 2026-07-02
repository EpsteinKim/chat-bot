package com.example.chatbot.application.activity

import com.example.chatbot.application.activity.port.`in`.ActivityStatsResult
import com.example.chatbot.application.activity.port.`in`.ActivityStatsUseCase
import com.example.chatbot.application.activity.port.`in`.RecordActivityUseCase
import com.example.chatbot.application.activity.port.out.ActivityLogRepository
import com.example.chatbot.application.common.AccessDeniedException
import com.example.chatbot.domain.activity.ActivityLog
import com.example.chatbot.domain.activity.ActivityType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class ActivityService(
    private val activityLogRepository: ActivityLogRepository,
    private val clock: Clock,
) : RecordActivityUseCase, ActivityStatsUseCase {

    @Transactional
    override fun record(userId: String, type: ActivityType) {
        activityLogRepository.save(
            ActivityLog(UUID.randomUUID().toString(), userId, type, Instant.now(clock))
        )
    }

    @Transactional(readOnly = true)
    override fun stats(isAdmin: Boolean): ActivityStatsResult {
        if (!isAdmin) throw AccessDeniedException("only admin can view activity stats")
        val to = Instant.now(clock)
        val from = to.minus(Duration.ofHours(24))
        val signupCount = activityLogRepository.countByTypeAndCreatedAtAfter(ActivityType.SIGNUP, from)
        val loginCount = activityLogRepository.countByTypeAndCreatedAtAfter(ActivityType.LOGIN, from)
        val chatCreatedCount = activityLogRepository.countByTypeAndCreatedAtAfter(ActivityType.CHAT_CREATED, from)
        return ActivityStatsResult(signupCount, loginCount, chatCreatedCount, from, to)
    }
}
