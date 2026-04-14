package com.exemple.ingredientspring.repository;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.CategoryEnum;
import com.exemple.ingredientspring.entity.DishIngredient;
import com.exemple.ingredientspring.entity.Ingredient;
import com.exemple.ingredientspring.entity.UnitEnum;
import com.exemple.ingredientspring.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Repository
public class DishIngredientRepository {
    private DataSource dataSource;
    private DishRepository dishRepository;
    public void detachAndAttachIngredients(Connection conn, Integer dishId, List<DishIngredient> ingredients) throws SQLException {
        PreparedStatement detachStatement = conn.prepareStatement("""
        DELETE FROM dish_ingredient WHERE id_dish = ?
        """);

        detachStatement.setInt(1, dishId);

        detachStatement.execute();

        String attachmentQuery = """
                INSERT INTO dish_ingredient ( id_dish, id_ingredient, required_quantity, unit)
                VALUES ( ?, ?, ?, ?::unit)
                """;

        try {
            PreparedStatement attachStatement = conn.prepareStatement(attachmentQuery);
            for(DishIngredient ingredient : ingredients) {
                attachStatement.clearParameters();
//                attachStatement.setInt(1, generateId.getNextSerialValue(conn, "dish_ingredient", "id"));
                attachStatement.setInt(1, dishId);
                attachStatement.setInt(2, ingredient.getIngredient().getId());
                attachStatement.setDouble(3, ingredient.getQuantity());
                attachStatement.setString(4, String.valueOf(ingredient.getUnit()));
                attachStatement.addBatch();
            }
            attachStatement.executeBatch();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }



    public List<DishIngredient> findIngredientsByDishId(int id, String ingredientName, Double ingredientPriceAround) throws NotFoundException {
        try (Connection conn = dataSource.getConnection()) {

            dishRepository.findDishById(id);

            StringBuilder sql = new StringBuilder("""
                SELECT di.id, di.required_quantity, di.unit,
                       i.id as ingredient_id, i.name as ingredient_name,
                       i.category, i.price as ingredient_price
                FROM dish_ingredient di
                JOIN ingredient i ON i.id = di.id_ingredient
                WHERE di.id_dish = ?
                """);

            if (ingredientName != null) {
                sql.append(" AND i.name ILIKE ?");
            }
            if (ingredientPriceAround != null) {
                sql.append(" AND i.price BETWEEN ? AND ?");
            }

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            ps.setInt(paramIndex++, id);

            if (ingredientName != null) {
                ps.setString(paramIndex++, "%" + ingredientName + "%");
            }
            if (ingredientPriceAround != null) {
                ps.setDouble(paramIndex++, ingredientPriceAround - 50);
                ps.setDouble(paramIndex++, ingredientPriceAround + 50);
            }

            ResultSet rs = ps.executeQuery();
            List<DishIngredient> ingredients = new ArrayList<>();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("ingredient_id"));
                ingredient.setName(rs.getString("ingredient_name"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredient.setPrice(rs.getDouble("ingredient_price"));

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setQuantity(rs.getDouble("required_quantity"));
                di.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                di.setIngredient(ingredient);
                ingredients.add(di);
            }

            return ingredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
