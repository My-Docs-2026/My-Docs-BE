package com.example.mydocsbe.auth.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2SuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider,
) : SimpleUrlAuthenticationSuccessHandler() {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val oAuth2Token = authentication as OAuth2AuthenticationToken

        val email = oAuth2User.getAttribute<String>("email") ?: ""
        val provider = oAuth2Token.authorizedClientRegistrationId
        val token = jwtTokenProvider.generateToken(email, provider)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write("""{"accessToken":"$token"}""")
    }
}
