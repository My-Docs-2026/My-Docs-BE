package com.example.mydocsbe.user.repository

import com.example.mydocsbe.user.domain.EmailVerification
import org.springframework.data.jpa.repository.JpaRepository

interface EmailVerificationRepository : JpaRepository<EmailVerification, String> {
    fun findByEmailAndCode(
        email: String,
        code: String,
    ): EmailVerification?

    fun findByEmail(email: String): EmailVerification?

    fun findByEmailAndVerified(
        email: String,
        verified: Boolean,
    ): EmailVerification?

    fun deleteByEmail(email: String)
}
