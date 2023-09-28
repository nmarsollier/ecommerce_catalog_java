package com.catalog.article.vo;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * Objeto valor para crear un articulo nuevo.
 */
public class NewData {
    @SerializedName("name")
    @Pattern(regexp = "^.{1,50}$", message = "Entre 1 y 50 caracteres.")
    public String name;

    @SerializedName("description")
    @Pattern(regexp = "^.{0,2048}$", message = "Valor muy largo.")
    public String description;

    @SerializedName("image")
    @Pattern(regexp = "^.{0,40}$", message = "Entre 1 y 40 caracteres.")
    public String image;

    @Min(value = 0, message = "Debe ser positivo.")
    @SerializedName("price")
    public double price;

    @SerializedName("stock")
    @Min(value = 0, message = "Debe ser positivo.")
    public int stock;

    public static NewData fromJson(String json) {
        return GsonTools.gson().fromJson(json, NewData.class);
    }
}
