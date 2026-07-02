package com.example.chatbot.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@EnableConfigurationProperties(OpenAiProperties::class, JwtProperties::class, AdminProperties::class)
class AppConfig {

    @Bean
    fun clock(): Clock = Clock.systemUTC()
}
