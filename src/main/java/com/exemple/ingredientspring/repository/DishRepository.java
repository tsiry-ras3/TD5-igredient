package com.exemple.ingredientspring.repository;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class DishRepository {
    private DataSource dataSource;
    public List<Dish> findAll(){
        List<Dish> dishes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                SELECT id, name, dish_type, price
                FROM dish
                """);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getDouble("price"));
                dishes.add(dish);
            }

            PreparedStatement ps2 = conn.prepareStatement("""
                SELECT di.id, di.required_quantity, di.unit,
                       di.id_dish,
                       i.id as ingredient_id, i.name as ingredient_name,
                       i.category, i.price as ingredient_price
                FROM dish_ingredient di
                JOIN ingredient i ON i.id = di.id_ingredient
                WHERE di.id_dish = ?
                """);

            for (Dish dish : dishes) {
                ps2.setInt(1, dish.getId());
                ResultSet rs2 = ps2.executeQuery();

                List<DishIngredient> ingredients = new ArrayList<>();
                while (rs2.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs2.getInt("ingredient_id"));
                    ingredient.setName(rs2.getString("ingredient_name"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs2.getString("category")));
                    ingredient.setPrice(rs2.getDouble("ingredient_price"));

                    DishIngredient di = new DishIngredient();
                    di.setId(rs2.getInt("id"));
                    di.setQuantity(rs2.getDouble("required_quantity"));
                    di.setUnit(UnitEnum.valueOf(rs2.getString("unit")));
                    di.setIngredient(ingredient);
                    di.setDish(dish);
                    ingredients.add(di);
                }
                dish.setIngredients(ingredients);
            }

            return dishes;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }}
