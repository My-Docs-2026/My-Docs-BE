package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.domain.Docs
import com.example.mydocsbe.docs.domain.enum.DocumentStatus
import com.example.mydocsbe.docs.domain.enum.DocumentType
import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.dto.request.UploadDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalysisDetailItem
import com.example.mydocsbe.docs.dto.response.AnalysisResult
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.example.mydocsbe.docs.dto.response.DocsStatusResponse
import com.example.mydocsbe.docs.dto.response.UploadDocsResponse
import com.example.mydocsbe.docs.repository.AnalysisRepository
import com.example.mydocsbe.docs.repository.UploadDocsRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class UploadDocsService(
    private val uploadDocsRepository: UploadDocsRepository,
    private val analysisRepository: AnalysisRepository,
    private val asyncAnalysisService: AsyncAnalysisService,
    private val objectMapper: ObjectMapper,
) {
    fun uploadDocs(req: UploadDocsRequest): UploadDocsResponse {
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

        asyncAnalysisService.analyzeAndStore(
            doc.id,
            AnalyzeDocsRequest(
                documentId = doc.id,
                title = doc.title,
                type = doc.type,
                fileUrl = doc.fileUrl,
                rawText = doc.rawText,
            ),
        )

        return UploadDocsResponse(
            document_id = doc.id,
            title = doc.title,
            type = doc.type.name.lowercase(),
            file_url = doc.fileUrl,
            status = doc.status.name,
            created_at = doc.createdAt.toString(),
        )
    }

    fun getStatus(docId: String): DocsStatusResponse {
        val doc = uploadDocsRepository.findById(docId)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }
        return DocsStatusResponse(document_id = doc.id, status = doc.status.name)
    }

    fun getAnalysis(docId: String): AnalyzeDocsResponse {
        val doc = uploadDocsRepository.findById(docId)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }

        val analysis = analysisRepository.findById(docId).orElse(null)?.let { a ->
            val details = objectMapper.readValue(a.analysisDetail, Array<AnalysisDetailItem>::class.java).toList()
            AnalysisResult(
                summary = a.summary,
                pros_summary = a.prosSummary,
                analysis_detail = details,
                analyzed_at = a.createdAt.toString(),
            )
        }

        return AnalyzeDocsResponse(
            document_id = doc.id,
            title = doc.title,
            type = doc.type.name.lowercase(),
            file_url = doc.fileUrl,
            status = doc.status.name,
            created_at = doc.createdAt.toString(),
            analysis = analysis,
        )
    }
}
