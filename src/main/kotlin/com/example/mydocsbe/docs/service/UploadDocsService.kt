package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.domain.Docs
import com.example.mydocsbe.docs.domain.enum.DocumentStatus
import com.example.mydocsbe.docs.domain.enum.DocumentType
import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.dto.request.UploadDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.example.mydocsbe.docs.repository.UploadDocsRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class UploadDocsService(
    private val uploadDocsRepository: UploadDocsRepository,
    private val analyzeDocsService: AnalyzeDocsService,
) {
    @Transactional
    fun uploadDocs(req: UploadDocsRequest): AnalyzeDocsResponse {
        val title =
            req.title?.takeIf { it.isNotBlank() }
                ?: "DOCS_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}"

        val userId =
            SecurityContextHolder.getContext().authentication?.name
                ?: throw IllegalStateException("인증 정보가 없습니다.")

        val fileUrl =
            when (req.type) {
                DocumentType.FILE -> req.file_url ?: throw IllegalArgumentException("FILE 타입은 file_url이 필요합니다.")
                DocumentType.TEXT -> null
            }

        val rawText =
            when (req.type) {
                DocumentType.TEXT -> req.raw_text ?: throw IllegalArgumentException("TEXT 타입은 raw_text가 필요합니다.")
                DocumentType.FILE -> null
            }

        val doc = uploadDocsRepository.save(
            Docs(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                type = req.type,
                fileUrl = fileUrl,
                rawText = rawText,
                status = DocumentStatus.PENDING,
            ),
        )

        val result = analyzeDocsService.analyze(
            AnalyzeDocsRequest(
                documentId = doc.id,
                title = doc.title,
                type = doc.type,
                fileUrl = doc.fileUrl,
                rawText = doc.rawText,
            ),
        )

        doc.status = DocumentStatus.COMPLETED
        return result
    }
}