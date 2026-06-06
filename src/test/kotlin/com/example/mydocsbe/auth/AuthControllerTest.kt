package com.example.mydocsbe.auth

import com.example.mydocsbe.auth.dto.LoginRequest
import com.example.mydocsbe.auth.dto.TokenResponse
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [AuthController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var authService: AuthService

    @Test
    fun `test@test_com과 1111로 로그인 성공 시 200과 accessToken 반환`() {
        given(authService.login(LoginRequest("test@test.com", "1111")))
            .willReturn(TokenResponse("jwt-token"))

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"test@test.com","password":"1111"}"""),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("jwt-token"))
    }

    @Test
    fun `잘못된 패스워드로 로그인 시 401 반환`() {
        willThrow(BadCredentialsException("Invalid credentials"))
            .given(authService).login(LoginRequest("test@test.com", "wrong"))

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email":"test@test.com","password":"wrong"}"""),
        )
            .andExpect(status().isUnauthorized)
    }
}
