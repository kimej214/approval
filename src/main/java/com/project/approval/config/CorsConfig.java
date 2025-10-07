package com.project.approval.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
 @Override
 public void addCorsMappings(CorsRegistry registry) {
     registry.addMapping("/api/**") // CORS 적용할 URL 패턴
             .allowedOrigins("http://localhost:5173") // 프론트 주소 (React dev server)
             .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
             .allowedHeaders("*") // 모든 헤더 허용 (Authorization, Content-Type 등)
             .allowCredentials(true) // 쿠키/세션 허용 (credentials: 'include')
             .maxAge(3600); // 프리플라이트 캐시 시간 (초) → 1시간
 }
}