package com.example.mydocsbe.docs.dto.response

class FileUploadResponse(
    val fileName: String,
    val fileSize: Long,
    val fileKey: String,
    val fileUrl: String,
    val mimeType: String,
)
