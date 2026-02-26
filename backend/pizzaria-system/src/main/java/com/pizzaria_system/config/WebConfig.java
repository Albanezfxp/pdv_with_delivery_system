package com.pizzaria_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração global para habilitar o Cross-Origin Resource Sharing (CORS).
 * * Permite que o frontend, rodando na porta 80 (padrão), se comunique com o
 * backend, rodando na porta 8080.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a configuração a todos os endpoints do backend
                .allowedOrigins("http://localhost", "http://localhost:80", "http://127.0.0.1", "http://127.0.0.1:80", "http://localhost:5173") // Domínios permitidos (seu frontend)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true) // Permite o envio de cookies de autenticação
                .maxAge(3600); // Tempo de cache da resposta Preflight
    }
}