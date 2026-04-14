package com.exemple.ingredientspring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockMovement {
    private Integer id;
    private StockValue stockValue;
    private MovementTypeEnum type;
    private Instant creationDate;
}
