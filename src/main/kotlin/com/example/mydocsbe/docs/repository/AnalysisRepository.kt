package com.example.mydocsbe.docs.repository

import com.example.mydocsbe.docs.domain.Analysis
import org.springframework.data.jpa.repository.JpaRepository

interface AnalysisRepository : JpaRepository<Analysis, String>
