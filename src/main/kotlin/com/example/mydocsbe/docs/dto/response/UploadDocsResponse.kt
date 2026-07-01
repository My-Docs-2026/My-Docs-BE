package com.example.mydocsbe.docs.dto.response

data class UploadDocsResponse(
    val documentId: String,
    val title: String,
    val type: String,
    val fileUrl: String?,
    val status: String,
    val createdAt: String,
)
