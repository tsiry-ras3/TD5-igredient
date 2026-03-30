package com.exemple.ingredientspring.repository;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.*;
import com.exemple.ingredientspring.exception.NotFoundException;
import com.exemple.ingredientspring.service.GenerateId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class DishRepository {
    private DataSource dataSource;
    private GenerateId generateId;
    private DishIngredientRepository dishIngredientRepository;

    public List<Dish> findAll() {
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

    }

    public Dish findDishById(Integer id) throws NotFoundException {

        Connection connection = dataSource.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select dish.id as dish_id, dish.name as dish_name, dish_type, dish.price as dish_price
                            from dish
                            where dish.id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null : resultSet.getDouble("dish_price"));
                dish.setIngredients(findDishIngredientByDishId(id));
                return dish;
            }
            dataSource.closeConnection(connection);
            throw new NotFoundException("Dish not found .id = " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DishIngredient> findDishIngredientByDishId(Integer idDish) {
        Connection connection = dataSource.getConnection();
        List<DishIngredient> ingredients = new ArrayList<>();
        try {

            PreparedStatement ps = connection.prepareStatement(
                    """
                        
                            SELECT i.id as ingredient_id,
                               i.name as ingredient_name,
                               i.price as ingredient_price,
                               i.category as ingredient_category,
                               di.id as dish_ingredient_id,
                               di.required_quantity as quantity_required,
                               di.unit as  ingredient_unit,
                               d.id as dish_id,
                               d.name as dish_name,
                               d.dish_type as dish_type,
                               d.price as dish_price
                        FROM ingredient i
                                 JOIN dish_ingredient di ON i.id = di.id_ingredient
                                 JOIN dish d ON d.id = di.id_dish
                        WHERE di.id_dish = ?
                        """
            );

            ps.setInt(1, idDish);

            ResultSet rs =  ps.executeQuery();

            while(rs.next()) {
                DishIngredient ingredient = new DishIngredient();
                ingredient.setId(rs.getInt("dish_ingredient_id"));
                ingredient.setDish(
                        new Dish(
                                rs.getInt("dish_id"),
                                rs.getString("dish_name"),
                                rs.getDouble("dish_price"),
                                DishTypeEnum.valueOf(rs.getString("dish_type")),
                                List.of()
                        )
                );
                ingredient.setIngredient(
                        new Ingredient(
                                rs.getInt("ingredient_id"),
                                rs.getString("ingredient_name"),
                                CategoryEnum.valueOf(rs.getString("ingredient_category")),
                                rs.getDouble("ingredient_price")
                        )
                );
                ingredient.setUnit(UnitEnum.valueOf(rs.getString("ingredient_unit")));
                ingredient.setQuantity(rs.getDouble("quantity_required"));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }


}
