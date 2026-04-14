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
public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovements;
}
