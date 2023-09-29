package com.catalog.rest.dto;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;

/**
 * Objeto valor para crear un articulo nuevo.
 */
public class NewData {
    @SerializedName("name")
    @NotEmpty
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("image")
    public String image;

    @SerializedName("price")
    public double price;

    @SerializedName("stock")
    public int stock;

    public static NewData fromJson(String json) {
        return GsonTools.gson().fromJson(json, NewData.class);
    }
}
