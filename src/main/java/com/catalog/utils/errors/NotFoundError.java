package com.catalog.utils.errors;

/**
 * Es un error simple que se puede serializar como Json.
 */
public class NotFoundError extends ValidationError {
    public NotFoundError(String field) {
        super.addPath(field, "Not found.");
    }
}