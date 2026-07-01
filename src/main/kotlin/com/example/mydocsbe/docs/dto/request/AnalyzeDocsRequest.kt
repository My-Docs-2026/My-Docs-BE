package com.example.mydocsbe.docs.dto.request

import com.example.mydocsbe.docs.domain.enum.DocumentType

data class AnalyzeDocsRequest(
    val documentId: String,
    val title: String,
    val type: DocumentType,
    val fileUrl: String?,
    val rawText: String?,
)