package com.example.mydocsbe.auth

import com.example.mydocsbe.auth.dto.LoginRequest
import com.example.mydocsbe.auth.dto.TokenResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): TokenResponse =
        authService.login(request)
}
