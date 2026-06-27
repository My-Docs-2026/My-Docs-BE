package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.domain.Analysis
import com.example.mydocsbe.docs.domain.enum.DocumentStatus
import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.repository.AnalysisRepository
import com.example.mydocsbe.docs.repository.UploadDocsRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class AsyncAnalysisService(
    private val analyzeDocsService: AnalyzeDocsService,
    private val uploadDocsRepository: UploadDocsRepository,
    private val analysisRepository: AnalysisRepository,
    private val objectMapper: ObjectMapper,
) {
    @Async
    fun analyzeAndStore(docId: String, request: AnalyzeDocsRequest) {
        val doc = uploadDocsRepository.findById(docId).orElse(null) ?: return
        doc.status = DocumentStatus.ANALYZING
        uploadDocsRepository.save(doc)

        try {
            val result = analyzeDocsService.analyze(request)
            val analysis = result.analysis!!

            analysisRepository.save(
                Analysis(
                    id = docId,
                    summary = analysis.summary,
                    prosSummary = analysis.pros_summary,
                    analysisDetail = objectMapper.writeValueAsString(analysis.analysis_detail),
                ),
            )

            val completed = uploadDocsRepository.findById(docId).orElse(null) ?: return
            completed.status = DocumentStatus.COMPLETED
            uploadDocsRepository.save(completed)
        } catch (e: Exception) {
            val failed = uploadDocsRepository.findById(docId).orElse(null) ?: return
            failed.status = DocumentStatus.FAILED
            uploadDocsRepository.save(failed)
        }
    }
}
