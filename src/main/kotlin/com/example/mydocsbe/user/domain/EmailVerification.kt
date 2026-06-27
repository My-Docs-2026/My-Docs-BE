package com.example.mydocsbe.user.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "email_verifications")
class EmailVerification(
    @Id
    var id: String = UUID.randomUUID().toString(),
    @Column(nullable = false)
    var email: String,
    @Column(nullable = false)
    var code: String,
    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,
    @Column(nullable = false)
    var verified: Boolean = false,
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),
)
