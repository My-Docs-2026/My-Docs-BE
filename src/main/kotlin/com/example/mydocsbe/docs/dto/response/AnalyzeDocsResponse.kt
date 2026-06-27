package com.example.mydocsbe.docs.dto.response

data class AnalyzeDocsResponse(
    val document_id: String,
    val title: String,
    val type: String,
    val file_url: String?,
    val status: String,
    val created_at: String,
    val analysis: AnalysisResult?,
)

data class AnalysisResult(
    val summary: String,
    val pros_summary: String,
    val analysis_detail: List<AnalysisDetailItem>,
    val analyzed_at: String,
)

data class AnalysisDetailItem(
    val risk_level: String,
    val clause_title: String,
    val original_text: String,
    val warning: String,
)