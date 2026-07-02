package com.example.mydocsbe.docs.repository

import com.example.mydocsbe.docs.domain.Docs
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UploadDocsRepository : JpaRepository<Docs, UUID> {
    fun findByUserId(userId: String, pageable: Pageable): Page<Docs>
}
