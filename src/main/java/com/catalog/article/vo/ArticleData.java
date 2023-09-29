package com.catalog.article.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Representacion publica del articulo
 */
public final class ArticleData {
    @SerializedName("_id")
    public final String id;

    @SerializedName("name")
    public final String name;

    @SerializedName("description")
    public final String description;

    @SerializedName("image")
    public final String image;

    @SerializedName("price")
    public final double price;

    @SerializedName("stock")
    public final int stock;

    @SerializedName("enabled")
    public final boolean enabled;

    public ArticleData(
            String id,
            String name,
            String description,
            String image,
            double price,
            int stock,
            boolean enabled
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.stock = stock;
        this.enabled = enabled;
    }
}