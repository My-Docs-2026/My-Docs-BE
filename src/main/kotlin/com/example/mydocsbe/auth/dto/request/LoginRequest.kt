package com.example.mydocsbe.auth.dto.request

data class LoginRequest(
    val email: String,
    val password: String,
)
