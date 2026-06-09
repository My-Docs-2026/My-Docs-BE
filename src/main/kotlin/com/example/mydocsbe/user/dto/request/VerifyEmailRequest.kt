package com.example.mydocsbe.user.dto.request

data class VerifyEmailRequest(
    val email: String,
    val code: String,
)
