package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.dto.response.FileUploadResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.*

@Service
class FileUploadService(
    private val s3Client: S3Client,
    @Value("\${cloudflare.r2.bucket}") private val bucket: String,
    @Value("\${cloudflare.r2.public-url}") private val publicUrl: String,
) {
    fun upload(file: MultipartFile): FileUploadResponse {
        val fileKey = "docs/${UUID.randomUUID()}/${file.originalFilename}"
        val mimeType = file.contentType ?: "application/octet-stream"

        s3Client.putObject(
            PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(mimeType)
                .contentLength(file.size)
                .build(),
            RequestBody.fromInputStream(file.inputStream, file.size),
        )

        return FileUploadResponse(
            fileName = file.originalFilename ?: "",
            fileSize = file.size,
            fileKey = fileKey,
            fileUrl = "$publicUrl/$fileKey",
            mimeType = mimeType,
        )
    }
}
