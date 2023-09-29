package com.catalog.utils;

import jakarta.validation.constraints.NotNull;

public final class StringTools {
    @NotNull
    public static String notNull(String value) {
        return notNull(value, "");
    }

    @NotNull
    public static String notNull(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String escapeForRegex(String input) {
        return input.replace("\\", "\\\\")
                .replace(".", "\\.")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("?", "\\?")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("|", "\\|");
    }
}
