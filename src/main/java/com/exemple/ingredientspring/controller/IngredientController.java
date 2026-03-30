package com.exemple.ingredientspring.controller;

import com.exemple.ingredientspring.entity.StockValue;
import com.exemple.ingredientspring.entity.UnitEnum;
import com.exemple.ingredientspring.exception.BadRequestException;
import com.exemple.ingredientspring.exception.NotFoundException;
import com.exemple.ingredientspring.repository.IngredientRepository;
import com.exemple.ingredientspring.validator.IngredientValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@AllArgsConstructor
public class IngredientController {
    IngredientRepository ingredientRepository;
    IngredientValidator validator;

    @GetMapping("/ingredients")
    public ResponseEntity<?> getIngredients() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ingredientRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<?> getIngredient(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ingredientRepository.findById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/ingredients/{id}/stock")
    public ResponseEntity<?> getIngredient(
            @PathVariable Integer id,
            @RequestParam(name = "at", required = false) String temporal,
            @RequestParam(name = "unit", required = false) String unit
    ){
        try {
            validator.getIngredientAtValidator(temporal, unit);
            Instant instant = Instant.parse(temporal);
            UnitEnum unitEnum = UnitEnum.valueOf(unit.toUpperCase());
            StockValue stock = ingredientRepository.getStockAt(id, instant, unitEnum);
            return ResponseEntity.status(HttpStatus.OK).body(stock);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
