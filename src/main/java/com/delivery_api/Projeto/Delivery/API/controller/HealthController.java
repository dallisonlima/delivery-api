package com.delivery_api.Projeto.Delivery.API.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Verifica o status da aplicação")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "Verifica o status da aplicação", description = "Retorna o status da aplicação, data e hora, nome do serviço e versão do Java.")
    @ApiResponse(responseCode = "200", description = "Aplicação está no ar")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", "Delivery API",
                "javaVersion", System.getProperty("java.version")
        );
    }

    @GetMapping("/api/info")
    @Operation(summary = "Verifica as informações da aplicação", description = "Retorna as informações da aplicação, como nome, versão, desenvolvedor, versão do Java e framework.")
    @ApiResponse(responseCode = "200", description = "Informações da aplicação retornadas com sucesso")
    public AppInfo info() {
        return new AppInfo(
                "Delivery Tech API",
                "1.0.0",
                "Dallison",
                "JDK 21",
                "Spring Boot 3.2.x"
        );
    }

    public record AppInfo(
            String application,
            String version,
            String developer,
            String javaVersion,
            String framework
    ) {}
}
