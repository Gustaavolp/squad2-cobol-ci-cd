package com.squad2.Squad2_cobol.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // Aplica CORS a todos os caminhos sob /api/v1/
                .allowedOrigins("http://localhost:4200") // Permite origem do frontend Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true) // Permite credenciais (cookies, etc.)
                .maxAge(3600); // Tempo de cache para a resposta pre-flight
    }
} 