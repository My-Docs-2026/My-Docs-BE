package com.example.mydocsbe.docs.controller

import com.example.mydocsbe.docs.dto.request.UploadDocsRequest
import com.example.mydocsbe.docs.dto.response.AnalyzeDocsResponse
import com.example.mydocsbe.docs.dto.response.OriginDocResponse
import com.example.mydocsbe.docs.dto.response.DocsListResponse
import com.example.mydocsbe.docs.dto.response.DocsStatusResponse
import com.example.mydocsbe.docs.dto.response.UploadDocsResponse
import com.example.mydocsbe.docs.service.UploadDocsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/docs")
class UploadDocsController(
    private val uploadDocsService: UploadDocsService,
) {
    @GetMapping
    fun getDocsList(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "9") pageSize: Int,
        @RequestParam(defaultValue = "latest") sort: String,
    ): DocsListResponse = uploadDocsService.getDocsList(pageNumber, pageSize, sort)

    @PostMapping("/upload")
    fun uploadDocs(
        @RequestBody request: UploadDocsRequest,
    ): UploadDocsResponse = uploadDocsService.uploadDocs(request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDoc(
        @PathVariable id: String,
    ) = uploadDocsService.deleteDoc(id)

    @PostMapping("/{id}/retry")
    fun retryAnalysis(
        @PathVariable id: String,
    ): DocsStatusResponse = uploadDocsService.retryAnalysis(id)

    @GetMapping("/{id}/status")
    fun getStatus(
        @PathVariable id: String,
    ): DocsStatusResponse = uploadDocsService.getStatus(id)

    @GetMapping("/{id}/origin")
    fun getOriginDoc(
        @PathVariable id: String,
    ): OriginDocResponse = uploadDocsService.getOriginDoc(id)

    @GetMapping("/{id}")
    fun getAnalysis(
        @PathVariable id: String,
    ): AnalyzeDocsResponse = uploadDocsService.getAnalysis(id)
}
