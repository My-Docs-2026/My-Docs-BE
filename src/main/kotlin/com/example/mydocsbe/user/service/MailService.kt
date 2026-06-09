package com.example.mydocsbe.user.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val from: String,
) {
    fun sendOtp(
        to: String,
        code: String,
    ) {
        val message = SimpleMailMessage()
        message.setFrom(from)
        message.setTo(to)
        message.subject = "[My-Docs] 이메일 인증 코드"
        message.text =
            """
            My-Docs 이메일 인증 코드입니다.

            인증 코드: $code

            5분 이내로 입력해주세요.
            """.trimIndent()
        mailSender.send(message)
    }
}
