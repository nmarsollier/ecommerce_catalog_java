package com.catalog.utils.errors;

import com.catalog.utils.gson.GsonTools;
import com.catalog.utils.gson.SkipSerialization;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Un error de validaciones de atributos de una clase.
 * Estos errores se pueden serializar como Json.
 */
public class ValidationError extends Error {

    @SkipSerialization
    public int statusCode = 400;

    @SerializedName("messages")
    final ArrayList<ValidationMessage> messages = new ArrayList<>();

    public ValidationError() {

    }

    public ValidationError(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public ValidationError addPath(String path, String message) {
        messages.add(new ValidationMessage(path, message));
        return this;
    }

    public String toJson() {
        return GsonTools.toJson(new SerializedMessage(messages));
    }

    static class ValidationMessage {
        ValidationMessage(String path, String message) {
            this.path = path;
            this.message = message;
        }

        @SerializedName("path")
        final String path;
        @SerializedName("message")
        final String message;

    }

    static class SerializedMessage {
        @SerializedName("messages")
        final ArrayList<ValidationMessage> messages;

        SerializedMessage(ArrayList<ValidationMessage> messages) {
            this.messages = messages;
        }
    }

    public int statusCode() {
        return statusCode;
    }
}