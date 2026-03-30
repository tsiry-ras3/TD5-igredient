package com.exemple.ingredientspring.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StockValue {
    private Double quantity;
    private UnitEnum unit;
}
