package com.catalog.rest.dto;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;

/**
 * Objeto valor para crear un articulo nuevo.
 */
public final class NewData {
    @SerializedName("name")
    @NotEmpty
    public final String name;

    @SerializedName("description")
    public final String description;

    @SerializedName("image")
    public final String image;

    @SerializedName("price")
    public final double price;

    @SerializedName("stock")
    public final int stock;

    public NewData(String name, String description, String image, double price, int stock) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.stock = stock;
    }

    public static NewData fromJson(String json) {
        return GsonTools.gson().fromJson(json, NewData.class);
    }
}
