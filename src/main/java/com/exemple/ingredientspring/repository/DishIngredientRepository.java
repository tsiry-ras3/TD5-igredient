package com.exemple.ingredientspring.repository;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.DishIngredient;
import com.exemple.ingredientspring.service.GenerateId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@AllArgsConstructor
@Repository
public class DishIngredientRepository {
    private GenerateId generateId;
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

}
