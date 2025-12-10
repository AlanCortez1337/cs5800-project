package com.alancortez.project.utils;

import com.alancortez.project.model.IngredientUnit;
import java.util.HashMap;

public class IngredientUnitFactory {
    private final HashMap<String, IngredientUnit> flyweights = new HashMap<>();
    private static IngredientUnitFactory instance = null;
    private IngredientUnitFactory() {}

    public static IngredientUnitFactory getInstance() {
        if (instance == null) {
            return new IngredientUnitFactory();
        }
        return instance;
    }

    public IngredientUnit getIngredientUnit(Double pricePerUnit, String unitOfMeasurement) {
        String key = "price-" + pricePerUnit + "-unit-" + unitOfMeasurement;

        if (!flyweights.containsKey(key)) {
            IngredientUnit ingredientUnit = new IngredientUnit();
            ingredientUnit.setPricePerUnit(pricePerUnit);
            ingredientUnit.setUnitOfMeasurement(unitOfMeasurement);
            flyweights.put(key, ingredientUnit);
        }
        return flyweights.get(key);
    }
}
