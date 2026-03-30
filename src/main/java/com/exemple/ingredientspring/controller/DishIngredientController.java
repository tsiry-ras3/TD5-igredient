package com.exemple.ingredientspring.controller;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.DishIngredient;
import com.exemple.ingredientspring.exception.BadRequestException;
import com.exemple.ingredientspring.exception.NotFoundException;
import com.exemple.ingredientspring.repository.DishIngredientRepository;
import com.exemple.ingredientspring.repository.DishRepository;
import com.exemple.ingredientspring.validator.DishIngredientValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.util.List;

@AllArgsConstructor
@RestController
public class DishIngredientController {
    private DishIngredientRepository dishIngredientRepository;
    private DataSource dataSource;
    private DishRepository dishRepository;
    private DishIngredientValidator validator;

    @PutMapping("/dishes/{id}/ingredients")
    public ResponseEntity<?> addIngredient(@PathVariable Integer id, @RequestBody (required = false) List<DishIngredient> ingredients) throws NotFoundException {
        try {
            dishRepository.findDishById(id);
            validator.bodyValidator(ingredients);
            Connection con = dataSource.getConnection();
            dishIngredientRepository.detachAndAttachIngredients(con, id, ingredients);
            return ResponseEntity.status(HttpStatus.OK).body(dishRepository.findDishById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
