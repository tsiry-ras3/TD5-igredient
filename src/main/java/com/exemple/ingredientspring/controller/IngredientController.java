package com.exemple.ingredientspring.controller;

import com.exemple.ingredientspring.entity.Ingredient;
import com.exemple.ingredientspring.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class IngredientController {
    IngredientRepository ingredientRepository;

    @GetMapping("/ingredients")
    public ResponseEntity<?> getIngredients() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ingredientRepository.findAll());
        } catch (Exception e) {
//            throw new RuntimeException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<?> getIngredient(@PathVariable Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ingredientRepository.findById(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/ingredient/{id}/stock")
    public ResponseEntity<?> getIngredient(
            @PathVariable String id,
            @RequestParam(name = "at") String temporal,
            @RequestParam(name = "unti") String unit
    ) {
        return null;
    }
}
