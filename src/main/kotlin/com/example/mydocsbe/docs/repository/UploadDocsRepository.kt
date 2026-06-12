package com.example.mydocsbe.docs.repository

import com.example.mydocsbe.docs.domain.Docs
import org.springframework.data.jpa.repository.JpaRepository

interface UploadDocsRepository : JpaRepository<Docs, String>
