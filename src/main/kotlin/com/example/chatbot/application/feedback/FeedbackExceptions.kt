package com.example.chatbot.application.feedback

class ChatNotFoundException(chatId: String) : RuntimeException("chat not found: $chatId")
class DuplicateFeedbackException(chatId: String) : RuntimeException("feedback already exists for chat: $chatId")
class FeedbackNotFoundException(feedbackId: String) : RuntimeException("feedback not found: $feedbackId")
