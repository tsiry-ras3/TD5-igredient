package com.exemple.ingredientspring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Dish {
    private Integer id;
    private String name;
    private Double price;
    private DishTypeEnum dishType;
    private List<DishIngredient> ingredients;
}
