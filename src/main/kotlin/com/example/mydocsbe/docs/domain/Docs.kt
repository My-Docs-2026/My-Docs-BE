package com.example.mydocsbe.docs.domain

import com.example.mydocsbe.docs.domain.enum.DocumentStatus
import com.example.mydocsbe.docs.domain.enum.DocumentType
import jakarta.persistence.*
import org.hibernate.annotations.ColumnTransformer
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "document")
class Docs(
    @Id
    val id: UUID,
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Column(nullable = false)
    val title: String,
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "upper(type::text)", write = "lower(?)::document_type")
    @Column(nullable = false, columnDefinition = "document_type")
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
    @ColumnTransformer(read = "upper(status::text)", write = "lower(?)::status_type")
    @Column(nullable = false, columnDefinition = "status_type")
    var status: DocumentStatus,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
