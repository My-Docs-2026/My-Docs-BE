package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.domain.Docs
import com.example.mydocsbe.docs.domain.enum.DocumentStatus
import com.example.mydocsbe.docs.domain.enum.DocumentType
import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.dto.request.UploadDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalysisDetailItem
import com.example.mydocsbe.docs.dto.response.AnalysisResult
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.example.mydocsbe.docs.dto.response.DocsListItemResponse
import com.example.mydocsbe.docs.dto.response.OriginDocResponse
import com.example.mydocsbe.docs.dto.response.DocsListResponse
import com.example.mydocsbe.docs.dto.response.DocsStatusResponse
import com.example.mydocsbe.docs.dto.response.UploadDocsResponse
import com.example.mydocsbe.docs.repository.AnalysisRepository
import com.example.mydocsbe.docs.repository.UploadDocsRepository
import com.example.mydocsbe.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
    private val userRepository: UserRepository,
) {
    fun uploadDocs(req: UploadDocsRequest): UploadDocsResponse {
        val title =
            req.title?.takeIf { it.isNotBlank() }
                ?: "DOCS_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}"

        val email =
            SecurityContextHolder.getContext().authentication?.name
                ?: throw IllegalStateException("인증 정보가 없습니다.")

        val userId = userRepository.findByEmail(email)?.id
            ?: throw IllegalStateException("유저를 찾을 수 없습니다: $email")

        val fileUrl =
            when (req.type) {
                DocumentType.FILE -> req.fileUrl ?: throw IllegalArgumentException("FILE 타입은 fileUrl이 필요합니다.")
                DocumentType.TEXT -> null
            }

        val rawText =
            when (req.type) {
                DocumentType.TEXT -> req.rawText ?: throw IllegalArgumentException("TEXT 타입은 rawText가 필요합니다.")
                DocumentType.FILE -> null
            }

        val docId = UUID.randomUUID()
        val doc = uploadDocsRepository.save(
            Docs(
                id = docId,
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
                documentId = doc.id.toString(),
                title = doc.title,
                type = doc.type,
                fileUrl = doc.fileUrl,
                rawText = doc.rawText,
            ),
        )

        return UploadDocsResponse(
            documentId = doc.id.toString(),
            title = doc.title,
            type = doc.type.name.lowercase(),
            fileUrl = doc.fileUrl,
            status = doc.status.name,
            createdAt = doc.createdAt.toString(),
        )
    }

    fun getDocsList(pageNumber: Int, pageSize: Int, sort: String): DocsListResponse {
        val email = SecurityContextHolder.getContext().authentication?.name
            ?: throw IllegalStateException("인증 정보가 없습니다.")
        val userId = userRepository.findByEmail(email)?.id
            ?: throw IllegalStateException("유저를 찾을 수 없습니다: $email")

        val sorting = when (sort) {
            "title" -> Sort.by(Sort.Direction.ASC, "title")
            else -> Sort.by(Sort.Direction.DESC, "createdAt")
        }
        val page = uploadDocsRepository.findByUserId(
            userId,
            PageRequest.of(pageNumber, pageSize, sorting),
        )
        val content = page.content.map { doc ->
            val summary = analysisRepository.findById(doc.id).orElse(null)?.summary
            DocsListItemResponse(
                documentId = doc.id.toString(),
                title = doc.title,
                status = doc.status.name,
                createdAt = doc.createdAt.toString(),
                summary = summary,
            )
        }
        return DocsListResponse(
            content = content,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            pageNumber = page.number,
            pageSize = page.size,
        )
    }

    fun deleteDoc(docId: String) {
        val email = SecurityContextHolder.getContext().authentication?.name
            ?: throw IllegalStateException("인증 정보가 없습니다.")
        val userId = userRepository.findByEmail(email)?.id
            ?: throw IllegalStateException("유저를 찾을 수 없습니다: $email")

        val uuid = UUID.fromString(docId)
        val doc = uploadDocsRepository.findById(uuid)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }

        if (doc.userId != userId) {
            throw IllegalArgumentException("삭제 권한이 없습니다.")
        }

        analysisRepository.findById(uuid).ifPresent { analysisRepository.delete(it) }
        uploadDocsRepository.delete(doc)
    }

    fun retryAnalysis(docId: String): DocsStatusResponse {
        val uuid = UUID.fromString(docId)
        val doc = uploadDocsRepository.findById(uuid)
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }

        if (doc.status != DocumentStatus.FAILED) {
            throw IllegalArgumentException("FAILED 상태인 문서만 재시도할 수 있습니다. 현재 상태: ${doc.status.name}")
        }

        analysisRepository.findById(uuid).ifPresent { analysisRepository.delete(it) }

        doc.status = DocumentStatus.PENDING
        uploadDocsRepository.save(doc)

        asyncAnalysisService.analyzeAndStore(
            uuid,
            AnalyzeDocsRequest(
                documentId = docId,
                title = doc.title,
                type = doc.type,
                fileUrl = doc.fileUrl,
                rawText = doc.rawText,
            ),
        )

        return DocsStatusResponse(documentId = docId, status = DocumentStatus.PENDING.name)
    }

    fun getOriginDoc(docId: String): OriginDocResponse {
        val doc = uploadDocsRepository.findById(UUID.fromString(docId))
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }
        return OriginDocResponse(
            documentId = doc.id.toString(),
            title = doc.title,
            type = doc.type.name.lowercase(),
            fileUrl = doc.fileUrl,
            fileName = doc.fileName,
            fileSize = doc.fileSize,
            mimeType = doc.mimeType,
            rawText = doc.rawText,
        )
    }

    fun getStatus(docId: String): DocsStatusResponse {
        val doc = uploadDocsRepository.findById(UUID.fromString(docId))
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }
        return DocsStatusResponse(documentId = doc.id.toString(), status = doc.status.name)
    }

    fun getAnalysis(docId: String): AnalyzeDocsResponse {
        val doc = uploadDocsRepository.findById(UUID.fromString(docId))
            .orElseThrow { IllegalArgumentException("문서를 찾을 수 없습니다: $docId") }

        val analysis = analysisRepository.findById(UUID.fromString(docId)).orElse(null)?.let { a ->
            val details = objectMapper.readValue(a.analysisDetail, Array<AnalysisDetailItem>::class.java).toList()
            AnalysisResult(
                summary = a.summary,
                prosSummary = a.prosSummary,
                analysisDetail = details,
                analyzedAt = a.createdAt.toString(),
            )
        }

        return AnalyzeDocsResponse(
            documentId = doc.id.toString(),
            title = doc.title,
            type = doc.type.name.lowercase(),
            fileUrl = doc.fileUrl,
            status = doc.status.name,
            createdAt = doc.createdAt.toString(),
            analysis = analysis,
        )
    }
}
