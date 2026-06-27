package com.example.mydocsbe.docs.controller

import com.example.mydocsbe.docs.dto.request.UploadDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.example.mydocsbe.docs.dto.response.DocsStatusResponse
import com.example.mydocsbe.docs.dto.response.UploadDocsResponse
import com.example.mydocsbe.docs.service.UploadDocsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/docs")
class UploadDocsController(
    private val uploadDocsService: UploadDocsService,
) {
    @PostMapping("/upload")
    fun uploadDocs(
        @RequestBody request: UploadDocsRequest,
    ): UploadDocsResponse = uploadDocsService.uploadDocs(request)

    @GetMapping("/{id}/status")
    fun getStatus(
        @PathVariable id: String,
    ): DocsStatusResponse = uploadDocsService.getStatus(id)

    @GetMapping("/{id}")
    fun getAnalysis(
        @PathVariable id: String,
    ): AnalyzeDocsResponse = uploadDocsService.getAnalysis(id)
}
