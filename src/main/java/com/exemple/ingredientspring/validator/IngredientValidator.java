package com.exemple.ingredientspring.validator;


import com.exemple.ingredientspring.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class IngredientValidator {
    public void getIngredientAtValidator(String at, String unit) throws BadRequestException {
        if (at == null|| unit == null){
            throw new  BadRequestException("Either mandatory query parameter `at` or `unit` is not provided.");
        }
    }
}
