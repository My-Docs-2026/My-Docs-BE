package com.example.mydocsbe.docs.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
@Table(name = "analysis")
class Analysis(
    @Id
    @Column(columnDefinition = "uuid")
    val id: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    val summary: String,
    @Column(name = "pros_summary", nullable = false, columnDefinition = "TEXT")
    val prosSummary: String,
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "analysis_detail", nullable = false, columnDefinition = "jsonb")
    val analysisDetail: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
