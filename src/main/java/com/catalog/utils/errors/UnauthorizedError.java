package com.catalog.utils.errors;

import com.google.gson.annotations.SerializedName;

/**
 * Es un error simple que se puede serializar como Json.
 */
public class UnauthorizedError extends Error {

    public SerializedMessage message() {
        return new SerializedMessage("Unauthorized");
    }

    public static class SerializedMessage {
        @SerializedName("error")
        final String error;

        SerializedMessage(String error) {
            this.error = error;
        }
    }
}