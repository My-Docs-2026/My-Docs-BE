package com.example.mydocsbe.docs.dto.response

data class DocsListItemResponse(
    val documentId: String,
    val title: String,
    val status: String,
    val createdAt: String,
    val summary: String?,
)

data class DocsListResponse(
    val content: List<DocsListItemResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val pageSize: Int,
)
