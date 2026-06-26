package com.example.mydocsbe.user.controller

import com.example.mydocsbe.user.dto.request.RegisterRequest
import com.example.mydocsbe.user.dto.request.SignUpRequest
import com.example.mydocsbe.user.dto.request.VerifyEmailRequest
import com.example.mydocsbe.user.dto.response.MessageResponse
import com.example.mydocsbe.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterRequest,
    ): MessageResponse {
        userService.register(request)
        return MessageResponse("인증 코드를 이메일로 발송했습니다.")
    }

    @PostMapping("/verify-email")
    fun verifyEmail(
        @RequestBody request: VerifyEmailRequest,
    ): MessageResponse {
        userService.verifyEmail(request)
        return MessageResponse("이메일 인증이 완료되었습니다.")
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(
        @RequestBody request: SignUpRequest,
    ): MessageResponse {
        userService.signUp(request)
        return MessageResponse("회원가입이 완료되었습니다.")
    }
}
