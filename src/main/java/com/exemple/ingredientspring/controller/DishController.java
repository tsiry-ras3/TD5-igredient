package com.exemple.ingredientspring.controller;

import com.exemple.ingredientspring.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DishController {

    private final DishRepository dishRepository;

    @GetMapping("/dishes")
    public ResponseEntity<?> getDishes() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(dishRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}