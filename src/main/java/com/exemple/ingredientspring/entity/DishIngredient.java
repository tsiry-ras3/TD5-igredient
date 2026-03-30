package com.exemple.ingredientspring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class DishIngredient {
    private Integer id;
    @JsonIgnore
    private Dish dish;
    private Ingredient ingredient;
    private double quantity;
    private UnitEnum unit;
}
