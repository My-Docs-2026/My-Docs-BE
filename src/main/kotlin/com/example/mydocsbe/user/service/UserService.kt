package com.example.mydocsbe.user.service

import com.example.mydocsbe.user.domain.EmailVerification
import com.example.mydocsbe.user.domain.User
import com.example.mydocsbe.user.dto.request.RegisterRequest
import com.example.mydocsbe.user.dto.request.VerifyEmailRequest
import com.example.mydocsbe.user.repository.EmailVerificationRepository
import com.example.mydocsbe.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.random.Random

@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailVerificationRepository: EmailVerificationRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailService: MailService,
) {
    @Transactional
    fun register(request: RegisterRequest) {
        val existing = userRepository.findByEmail(request.email)
        if (existing != null && existing.emailVerified) {
            throw IllegalStateException("이미 사용 중인 이메일입니다.")
        }

        if (existing == null) {
            userRepository.save(
                User(
                    id = UUID.randomUUID().toString(),
                    email = request.email,
                    password = passwordEncoder.encode(request.password)!!,
                ),
            )
        }

        emailVerificationRepository.deleteByEmail(request.email)

        val code = "%06d".format(Random.nextInt(1_000_000))
        emailVerificationRepository.save(
            EmailVerification(
                email = request.email,
                code = code,
                expiresAt = Instant.now().plusSeconds(300),
            ),
        )

        mailService.sendOtp(request.email, code)
    }

    @Transactional
    fun verifyEmail(request: VerifyEmailRequest) {
        val verification =
            emailVerificationRepository.findByEmailAndCode(request.email, request.code)
                ?: throw IllegalArgumentException("잘못된 인증 코드입니다.")

        if (Instant.now().isAfter(verification.expiresAt)) {
            emailVerificationRepository.delete(verification)
            throw IllegalArgumentException("만료된 인증 코드입니다. 다시 요청해주세요.")
        }

        val user =
            userRepository.findByEmail(request.email)
                ?: throw IllegalStateException("사용자를 찾을 수 없습니다.")

        user.emailVerified = true
        emailVerificationRepository.delete(verification)
    }
}
