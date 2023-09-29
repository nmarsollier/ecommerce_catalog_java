package com.catalog.utils.errors;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.util.Pair;

public class Validate {
    public static void min(@NotNull String fieldName, int value, int min) {
        if (value < min) throw new ValidationError(Pair.of(fieldName, "Debe ser mayor a " + min + "."));
    }

    public static void min(@NotNull String fieldName, double value, int min) {
        if (value < min) throw new ValidationError(Pair.of(fieldName, "Debe ser mayor a " + min + "."));
    }

    public static void minLen(@NotNull String fieldName, @NotNull String value, int min) {
        if (value.length() < min)
            throw new ValidationError(Pair.of(fieldName, "Debe ser mayor a " + min + " caracteres."));
    }

    public static void maxLen(@NotNull String fieldName, @NotNull String value, int max) {
        if (value.length() > max)
            throw new ValidationError(Pair.of(fieldName, "Debe ser menor a " + max + " caracteres."));
    }

    public static void notEmpty(@NotNull String fieldName, String value) {
        if (value == null || value.isEmpty()) {
            throw new ValidationError(Pair.of(fieldName, "No puede estar vacío."));
        }
    }
}
