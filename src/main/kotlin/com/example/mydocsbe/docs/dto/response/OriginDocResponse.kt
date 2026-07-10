package com.example.mydocsbe.docs.dto.response

data class OriginDocResponse(
    val documentId: String,
    val title: String,
    val type: String,
    val fileUrl: String?,
    val fileName: String?,
    val fileSize: Long?,
    val mimeType: String?,
    val rawText: String?,
)
