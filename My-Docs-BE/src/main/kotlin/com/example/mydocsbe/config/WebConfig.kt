package com.example.mydocsbe.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // 모든 경로에 대해
            .allowedOrigins(
                "https://my-docs.site", // 실제 운영 도메인
                "http://localhost:3000", // 로컬 개발용 (React 기본 포트)
                "http://localhost:5173"  // 로컬 개발용 (Vite 기본 포트)
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 허용할 HTTP 메서드
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true) // 쿠키/인증 정보 허용 필요시
            .maxAge(3600) // 프리플라이트 요청 캐싱 시간 (초)
    }
}
