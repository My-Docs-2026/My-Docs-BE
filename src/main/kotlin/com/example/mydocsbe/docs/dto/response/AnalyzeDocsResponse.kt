package com.example.mydocsbe.docs.dto.response

data class AnalyzeDocsResponse(
    val documentId: String,
    val title: String,
    val type: String,
    val fileUrl: String?,
    val status: String,
    val createdAt: String,
    val analysis: AnalysisResult?,
)

data class AnalysisResult(
    val summary: String,
    val prosSummary: String,
    val analysisDetail: List<AnalysisDetailItem>,
    val analyzedAt: String,
)

data class AnalysisDetailItem(
    val riskLevel: String,
    val clauseTitle: String,
    val originalText: String,
    val warning: String,
    val pageNumber: Int?,
)
