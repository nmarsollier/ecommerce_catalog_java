package com.catalog.article.vo;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

/**
 * Objeto valor para artículos.
 */
public class ArticleData {
    @SerializedName("_id")
    public String id;

    @SerializedName("name")
    @Pattern(regexp = "^.{1,50}$", message = "Entre 1 y 50 caracteres.")
    public String name;

    @SerializedName("description")
    @Pattern(regexp = "^.{0,2048}$", message = "Valor muy largo.")
    public String description;

    @SerializedName("image")
    @Pattern(regexp = "^.{0,40}$", message = "Entre 1 y 50 caracteres.")
    public String image;

    @SerializedName("price")
    @Min(value = 0, message = "Debe ser positivo.")
    public double price;

    @SerializedName("stock")
    @Min(value = 0, message = "Debe ser positivo.")
    public int stock;

    @SerializedName("enabled")
    public boolean enabled = true;
}