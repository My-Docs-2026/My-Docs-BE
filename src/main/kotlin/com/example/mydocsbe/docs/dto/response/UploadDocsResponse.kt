package com.example.mydocsbe.docs.dto.response

data class UploadDocsResponse(
    val document_id: String,
    val title: String,
    val type: String,
    val file_url: String?,
    val status: String,
    val created_at: String,
)
