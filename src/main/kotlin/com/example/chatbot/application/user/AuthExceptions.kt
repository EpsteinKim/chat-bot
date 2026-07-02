package com.example.chatbot.application.user

class EmailAlreadyExistsException(email: String) :
    RuntimeException("email already registered: $email")

class InvalidCredentialsException :
    RuntimeException("invalid email or password")
