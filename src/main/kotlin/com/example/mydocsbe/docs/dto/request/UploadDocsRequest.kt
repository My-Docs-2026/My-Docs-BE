package com.example.mydocsbe.docs.dto.request

import com.example.mydocsbe.docs.domain.enum.DocumentType

class UploadDocsRequest(
    val title: String?,
    val type: DocumentType,
    val fileUrl: String?,
    val rawText: String?,
)
