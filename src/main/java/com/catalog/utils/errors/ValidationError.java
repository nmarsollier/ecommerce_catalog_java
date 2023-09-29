package com.catalog.utils.errors;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.util.Pair;

import java.util.ArrayList;

/**
 * Un error de validaciones de atributos de una clase.
 * Estos errores se pueden serializar como Json.
 */
public class ValidationError extends Error {
    @SerializedName("messages")
    public final ArrayList<ValidationMessage> messages = new ArrayList<>();

    @SafeVarargs
    public ValidationError(@NotNull Pair<String, String>... pathMessage) {
        for (Pair<String, String> m : pathMessage) {
            addPath(m.getFirst(), m.getSecond());
        }
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public ValidationError addPath(String path, String message) {
        messages.add(new ValidationMessage(path, message));
        return this;
    }

    public SerializedMessage message() {
        return new SerializedMessage(messages);
    }

    public static class ValidationMessage {
        ValidationMessage(String path, String message) {
            this.path = path;
            this.message = message;
        }

        @SerializedName("path")
        public final String path;
        @SerializedName("message")
        public final String message;
    }

    public static class SerializedMessage {
        @SerializedName("messages")
        public final ArrayList<ValidationMessage> messages;

        SerializedMessage(ArrayList<ValidationMessage> messages) {
            this.messages = messages;
        }
    }
}