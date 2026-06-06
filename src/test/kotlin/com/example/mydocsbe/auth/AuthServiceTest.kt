package com.example.mydocsbe.auth

import com.example.mydocsbe.auth.dto.LoginRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class AuthServiceTest {

    private val authenticationManager: AuthenticationManager = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val authService = AuthService(authenticationManager, jwtTokenProvider)

    @Test
    fun `test@test_com과 1111로 로그인 성공 시 JWT 반환`() {
        val authenticated = UsernamePasswordAuthenticationToken("test@test.com", null, emptyList())
        whenever(authenticationManager.authenticate(any())).thenReturn(authenticated)
        whenever(jwtTokenProvider.generateToken("test@test.com", "local")).thenReturn("jwt-token")

        val result = authService.login(LoginRequest("test@test.com", "1111"))

        assertThat(result.accessToken).isEqualTo("jwt-token")
        verify(authenticationManager).authenticate(
            UsernamePasswordAuthenticationToken("test@test.com", "1111"),
        )
    }
}
