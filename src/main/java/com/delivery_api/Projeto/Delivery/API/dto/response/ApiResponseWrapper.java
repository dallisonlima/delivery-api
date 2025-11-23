package com.delivery_api.Projeto.Delivery.API.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Wrapper para encapsular respostas da API")
public class ApiResponseWrapper<T> {

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private final boolean success;

    @Schema(description = "Dados da Resposta")
    private final T data;

    @Schema(description = "Mensagem de resposta", example = "Operação realizada com sucesso")
    private final String message;

    @Schema(description = "Timestamp da resposta", example = "2023-10-01T12:00:00")
    private final LocalDateTime timestamp;

    private ApiResponseWrapper(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponseWrapper<T> success(T data, String message) {
        return new ApiResponseWrapper<>(true, data, message);
    }

    public static <T> ApiResponseWrapper<T> success(T data) {
        return new ApiResponseWrapper<>(true, data, "Operação realizada com sucesso.");
    }
}
