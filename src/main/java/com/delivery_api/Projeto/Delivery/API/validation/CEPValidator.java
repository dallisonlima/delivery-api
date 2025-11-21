package com.delivery_api.Projeto.Delivery.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    private static final String CEP_PATTERN = "\\d{5}-\\d{3}";

    @Override
    public void initialize(ValidCEP constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.isEmpty()) {
            return true; // Campos nulos ou vazios são considerados válidos para não sobrepor @NotBlank
        }
        return cep.matches(CEP_PATTERN);
    }
}
