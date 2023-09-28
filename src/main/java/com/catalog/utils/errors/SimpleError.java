package com.catalog.utils.errors;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;

/**
 * Es un error simple que se puede serializar como Json.
 */
public class SimpleError extends Error {

    @SerializedName("error")
    final String error;

    public SimpleError(String error) {
        this.error = error;
    }

    public String toJson() {
        return GsonTools.toJson(new SerializedMessage(error));
    }

    static class SerializedMessage {
        @SerializedName("error")
        final String error;

        SerializedMessage(String error) {
            this.error = error;
        }
    }
}