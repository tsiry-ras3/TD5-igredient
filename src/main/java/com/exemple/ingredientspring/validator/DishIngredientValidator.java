package com.exemple.ingredientspring.validator;

import com.exemple.ingredientspring.entity.DishIngredient;
import com.exemple.ingredientspring.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DishIngredientValidator {
    public void bodyValidator(List<DishIngredient> ingredients) throws BadRequestException {
        if(ingredients == null){
            throw new BadRequestException("Body is required");
        }
    }
}
