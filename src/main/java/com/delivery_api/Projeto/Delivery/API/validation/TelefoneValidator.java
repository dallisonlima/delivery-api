package com.delivery_api.Projeto.Delivery.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    private static final String TELEFONE_PATTERN = "^\\d{10,11}$";

    @Override
    public void initialize(ValidTelefone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.isEmpty()) {
            return true; // Campos nulos ou vazios são considerados válidos para não sobrepor @NotBlank
        }
        return telefone.matches(TELEFONE_PATTERN);
    }
}
