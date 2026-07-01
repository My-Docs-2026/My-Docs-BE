package com.example.mydocsbe.docs.controller

import com.example.mydocsbe.docs.dto.request.AnalyzeDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.example.mydocsbe.docs.service.AnalyzeDocsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/docs")
class AnalyzeDocsController(
    private val analyzeDocsService: AnalyzeDocsService,
) {
    @PostMapping("/analyze")
    fun analyze(
        @RequestBody request: AnalyzeDocsRequest,
    ): AnalyzeDocsResponse = analyzeDocsService.analyze(request)
}