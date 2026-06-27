package com.example.mydocsbe.docs.service

import com.example.mydocsbe.docs.domain.enum.DocumentType
import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalysisDetailItem
import com.example.mydocsbe.docs.dto.response.AnalysisResult
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
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
) {
    private val severityMap = mapOf("상" to "High", "중" to "Medium", "하" to "Low")
    private val httpClient: HttpClient = HttpClient.newHttpClient()

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

    fun analyze(request: AnalyzeDocsRequest): AnalyzeDocsResponse {
        val messageContent = buildMessageContent(request)

        val requestBody = mapOf(
            "model" to "claude-sonnet-4-6",
            "max_tokens" to 2000,
            "system" to systemPrompt,
            "messages" to listOf(mapOf("role" to "user", "content" to messageContent)),
        )

        val responseBody = callClaudeApi(objectMapper.writeValueAsString(requestBody))
        val claudeResponse = objectMapper.readValue(responseBody, ClaudeApiResponse::class.java)

        val rawText = claudeResponse.content
            .filter { it.type == "text" }
            .mapNotNull { it.text }
            .joinToString("")

        val cleaned = rawText.replace(Regex("```json|```"), "").trim()
        val claudeResult = objectMapper.readValue(cleaned, ClaudeAnalysisResult::class.java)

        return AnalyzeDocsResponse(
            documentId = request.documentId,
            title = request.title,
            type = request.type.name.lowercase(),
            fileUrl = request.fileUrl,
            status = "COMPLETED",
            createdAt = Instant.now().toString(),
            analysis = AnalysisResult(
                summary = claudeResult.summary,
                prosSummary = claudeResult.pros_summary,
                analysisDetail = claudeResult.risks.map { risk ->
                    AnalysisDetailItem(
                        riskLevel = severityMap[risk.severity] ?: "Low",
                        clauseTitle = risk.title,
                        originalText = risk.summary,
                        warning = risk.reason,
                    )
                },
                analyzedAt = Instant.now().toString(),
            ),
        )
    }

    private fun callClaudeApi(body: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.anthropic.com/v1/messages"))
            .header("Content-Type", "application/json")
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw RuntimeException("Claude API 오류 (${response.statusCode()}): ${response.body()}")
        }
        return response.body()
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
        URI(url).toURL().openStream().use { it.readBytes() }
}
