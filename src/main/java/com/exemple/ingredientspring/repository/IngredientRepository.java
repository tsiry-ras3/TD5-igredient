package com.exemple.ingredientspring.repository;

import com.exemple.ingredientspring.datasource.DataSource;
import com.exemple.ingredientspring.entity.CategoryEnum;
import com.exemple.ingredientspring.entity.Ingredient;
import com.exemple.ingredientspring.entity.StockValue;
import com.exemple.ingredientspring.entity.UnitEnum;
import com.exemple.ingredientspring.exception.BadRequestException;
import com.exemple.ingredientspring.exception.NotFoundException;
import com.exemple.ingredientspring.validator.IngredientValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class IngredientRepository {
    private DataSource dataSource;
    private IngredientValidator validator;

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


    public StockValue getStockAt(int id, Instant at, UnitEnum unit) throws NotFoundException, BadRequestException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement checkPs = conn.prepareStatement("""
                SELECT id FROM ingredient WHERE id = ?
                """);
            checkPs.setInt(1, id);
            ResultSet checkRs = checkPs.executeQuery();
            if (!checkRs.next()) {
                throw new NotFoundException("Ingredient.id=" + id + " is not found");
            }

            PreparedStatement ps = conn.prepareStatement("""
                SELECT quantity, type
                FROM stock_movement
                WHERE id_ingredient = ?
                AND creation_datetime <= ?
                ORDER BY creation_datetime ASC
                """);
            ps.setInt(1, id);
            ps.setTimestamp(2, Timestamp.from(at));
            ResultSet rs = ps.executeQuery();

            double total = 0;
            while (rs.next()) {
                double qty = rs.getDouble("quantity");
                String type = rs.getString("type");
                if ("IN".equals(type)) {
                    total += qty;
                } else {
                    total -= qty;
                }
            }

            return new StockValue(total, unit);

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

