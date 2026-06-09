package com.example.mydocsbe.auth.service

import com.example.mydocsbe.auth.dto.request.LoginRequest
import com.example.mydocsbe.auth.dto.response.TokenResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun login(request: LoginRequest): TokenResponse {
        val auth =
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password),
            )
        val token = jwtTokenProvider.generateToken(auth.name, "local")
        return TokenResponse(token)
    }
}
