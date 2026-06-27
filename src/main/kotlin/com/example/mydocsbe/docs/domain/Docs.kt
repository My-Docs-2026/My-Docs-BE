package com.example.mydocsbe.docs.domain

import com.example.mydocsbe.docs.domain.enum.DocumentStatus
import com.example.mydocsbe.docs.domain.enum.DocumentType
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "document")
class Docs(
    @Id
    @Column(columnDefinition = "uuid")
    val id: String,
    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    val userId: String,
    @Column(nullable = false)
    val title: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: DocumentType,
    @Column(name = "file_url")
    val fileUrl: String? = null,
    @Column(name = "file_name")
    val fileName: String? = null,
    @Column(name = "file_key")
    val fileKey: String? = null,
    @Column(name = "file_size")
    val fileSize: Long? = null,
    @Column(name = "mime_type")
    val mimeType: String? = null,
    @Column(name = "raw_text", columnDefinition = "TEXT")
    val rawText: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DocumentStatus,
    @Column(name = "analysis_json", columnDefinition = "TEXT")
    var analysisJson: String? = null,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
