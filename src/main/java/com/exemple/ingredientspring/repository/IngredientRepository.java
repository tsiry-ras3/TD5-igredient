package com.exemple.ingredientspring.repository;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.CategoryEnum;
import com.exemple.ingredientspring.entity.Ingredient;
import com.exemple.ingredientspring.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class IngredientRepository {
    private DataSource dataSource;

    public Ingredient findById(int id) throws NotFoundException {
        try (
                Connection conn = dataSource.getConnection()) {
            PreparedStatement ingredientStatement = conn.prepareStatement("""
                            SELECT id, name, category, price
                            FROM ingredient WHERE id = ?
                    """);
            ingredientStatement.setInt(1, id);
            ResultSet rs = ingredientStatement.executeQuery();

            if (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredient.setPrice(rs.getDouble("price"));
                return ingredient;
            }

            throw new NotFoundException("Ingredient not found: " + id);

//        PreparedStatement movementStatement = conn.prepareStatement("""
//                    SELECT id, quantity, unit, type, creation_datetime
//                    FROM stockmovement
//                    WHERE id_ingredient = ?
//                """);
//        movementStatement.setInt(1, id);
//        ResultSet rsMovement = movementStatement.executeQuery();
//
//        List<StockMovement> stockMovementList = new ArrayList<>();
//        while (rsMovement.next()) {
//            stockMovementList.add(new StockMovement(
//                    rsMovement.getInt("id"),
//                    new StockValue(
//                            rsMovement.getDouble("quantity"),
//                            Unit.valueOf(rsMovement.getString("unit"))
//                    ),
//                    MovementTypeEnum.valueOf(rsMovement.getString("type")),
//                    rsMovement.getTimestamp("creation_datetime").toInstant()
//            ));
//        }
//        ingredient.setStockMovementList(stockMovementList);

        }catch (NotFoundException e){
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findAll() {

        try (
                Connection conn = dataSource.getConnection()) {
            PreparedStatement ingredientStatement = conn.prepareStatement("""
                            SELECT id, name, category, price
                            FROM ingredient
                    """);
            ResultSet rs = ingredientStatement.executeQuery();

            List<Ingredient> ingredients = new ArrayList<>();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredient.setPrice(rs.getDouble("price"));
                ingredients.add(ingredient);
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

