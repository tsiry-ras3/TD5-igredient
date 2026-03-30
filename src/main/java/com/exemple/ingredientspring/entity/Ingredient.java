package com.exemple.ingredientspring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
}
