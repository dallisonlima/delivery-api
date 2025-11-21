package com.delivery_api.Projeto.Delivery.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

    // Valida o formato HH:MM-HH:MM, com HH de 00 a 23 e MM de 00 a 59
    private static final String HORARIO_PATTERN = "^([01]\\d|2[0-3]):([0-5]\\d)-([01]\\d|2[0-3]):([0-5]\\d)$";

    @Override
    public void initialize(ValidHorarioFuncionamento constraintAnnotation) {
    }

    @Override
    public boolean isValid(String horario, ConstraintValidatorContext context) {
        if (horario == null || horario.isEmpty()) {
            return true; // Campos nulos ou vazios são considerados válidos para não sobrepor @NotBlank
        }
        return horario.matches(HORARIO_PATTERN);
    }
}
