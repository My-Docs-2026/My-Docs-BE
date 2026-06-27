package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.domain.enum.DocumentType
import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalysisDetailItem
import com.example.mydocsbe.docs.dto.response.AnalysisResult
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.time.Instant
import java.util.Base64

// Claude API 응답 파싱용 내부 DTO
private data class ClaudeApiResponse(val content: List<ClaudeContent>)
private data class ClaudeContent(val type: String, val text: String?)
private data class ClaudeAnalysisResult(
    val product_name: String?,
    val summary: String,
    val pros_summary: String,
    val risks: List<ClaudeRisk>,
)
private data class ClaudeRisk(
    val title: String,
    val severity: String,
    val summary: String,
    val reason: String,
)

@Service
class AnalyzeDocsService(
    @Value("\${anthropic.api-key}") private val apiKey: String,
    private val objectMapper: ObjectMapper,
    restClientBuilder: RestClient.Builder,
) {
    private val severityMap = mapOf("상" to "High", "중" to "Medium", "하" to "Low")

    private val systemPrompt = """
        당신은 금융 상품 설명서를 분석하는 전문가입니다.
        문서에서 소비자에게 불리하거나 주의가 필요한 내용을 찾아 반드시 JSON 형식으로만 응답하세요.
        마크다운 코드블록이나 설명 텍스트 없이 순수 JSON만 출력하세요.

        출력 형식:
        {
          "product_name": "상품명",
          "summary": "전반적인 상품 요약 (위험 항목 개수 포함)",
          "pros_summary": "소비자에게 유리한 점 요약",
          "risks": [
            {
              "title": "제목",
              "severity": "상|중|하",
              "summary": "요약",
              "reason": "위험 혹은 중요한 이유"
            }
          ]
        }
    """.trimIndent()

    private val claudeClient: RestClient = restClientBuilder
        .baseUrl("https://api.anthropic.com")
        .defaultHeader("x-api-key", apiKey)
        .defaultHeader("anthropic-version", "2023-06-01")
        .build()

    fun analyze(request: AnalyzeDocsRequest): AnalyzeDocsResponse {
        val messageContent = buildMessageContent(request)

        val requestBody = mapOf(
            "model" to "claude-sonnet-4-6",
            "max_tokens" to 2000,
            "system" to systemPrompt,
            "messages" to listOf(mapOf("role" to "user", "content" to messageContent)),
        )

        val claudeResponse = claudeClient.post()
            .uri("/v1/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(ClaudeApiResponse::class.java)
            ?: throw RuntimeException("Claude API 응답 없음")

        val rawText = claudeResponse.content
            .filter { it.type == "text" }
            .mapNotNull { it.text }
            .joinToString("")

        val cleaned = rawText.replace(Regex("```json|```"), "").trim()
        val claudeResult = objectMapper.readValue(cleaned, ClaudeAnalysisResult::class.java)

        return AnalyzeDocsResponse(
            document_id = request.documentId,
            title = request.title,
            type = request.type.name.lowercase(),
            file_url = request.fileUrl,
            status = "COMPLETED",
            created_at = Instant.now().toString(),
            analysis = AnalysisResult(
                summary = claudeResult.summary,
                pros_summary = claudeResult.pros_summary,
                analysis_detail = claudeResult.risks.map { risk ->
                    AnalysisDetailItem(
                        risk_level = severityMap[risk.severity] ?: "Low",
                        clause_title = risk.title,
                        original_text = risk.summary,
                        warning = risk.reason,
                    )
                },
                analyzed_at = Instant.now().toString(),
            ),
        )
    }

    private fun buildMessageContent(request: AnalyzeDocsRequest): List<Map<String, Any>> =
        when (request.type) {
            DocumentType.FILE -> {
                val base64 = Base64.getEncoder().encodeToString(downloadFile(request.fileUrl!!))
                listOf(
                    mapOf(
                        "type" to "document",
                        "source" to mapOf(
                            "type" to "base64",
                            "media_type" to "application/pdf",
                            "data" to base64,
                        ),
                    ),
                    mapOf("type" to "text", "text" to "이 금융 상품 설명서에서 소비자에게 불리한 내용을 찾아 JSON으로 반환해주세요."),
                )
            }
            DocumentType.TEXT -> listOf(
                mapOf(
                    "type" to "text",
                    "text" to "다음 금융 상품 설명서 내용을 분석해주세요:\n\n${request.rawText}\n\n소비자에게 불리한 내용을 찾아 JSON으로 반환해주세요.",
                ),
            )
        }

    private fun downloadFile(url: String): ByteArray =
        java.net.URI(url).toURL().openStream().use { it.readBytes() }
}