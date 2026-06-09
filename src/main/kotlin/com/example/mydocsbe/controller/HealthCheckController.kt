package com.example.mydocsbe.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class HealthCheckController {
    @GetMapping("/api/ping")
    fun ping(): Map<String, Any> =
        mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now(),
            "message" to "My-Docs-BE is running",
        )
}
