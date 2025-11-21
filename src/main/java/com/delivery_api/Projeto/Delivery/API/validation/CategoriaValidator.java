package com.delivery_api.Projeto.Delivery.API.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class CategoriaValidator implements ConstraintValidator<ValidCategoria, String> {

    private final List<String> allowedCategories = Arrays.asList(
            "Pizza", "Japonesa", "Lanches", "Brasileira", "Bebida", "Sobremesa"
    );

    @Override
    public void initialize(ValidCategoria constraintAnnotation) {
    }

    @Override
    public boolean isValid(String categoria, ConstraintValidatorContext context) {
        if (categoria == null || categoria.isEmpty()) {
            return true; // Deixe @NotBlank cuidar disso
        }
        return allowedCategories.contains(categoria);
    }
}
