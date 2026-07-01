package com.example.mydocsbe.docs.repository

import com.example.mydocsbe.docs.domain.Analysis
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AnalysisRepository : JpaRepository<Analysis, UUID>
