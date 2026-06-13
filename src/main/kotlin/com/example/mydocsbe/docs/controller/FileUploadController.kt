package com.example.mydocsbe.docs.controller

import com.example.mydocsbe.docs.dto.response.FileUploadResponse
import com.example.mydocsbe.docs.service.FileUploadService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/docs")
class FileUploadController(
    private val fileUploadService: FileUploadService,
) {
    @PostMapping("/file", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestPart file: MultipartFile,
    ): FileUploadResponse = fileUploadService.upload(file)
}
