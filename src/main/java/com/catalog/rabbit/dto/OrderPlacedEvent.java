package com.catalog.rabbit.dto;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class OrderPlacedEvent {
    @SerializedName("orderId")
    public String orderId;
    @SerializedName("cartId")
    public String cartId;
    @SerializedName("articles")
    public Article[] articles;

    public static class Article {
        @SerializedName("articleId")
        @NotEmpty(message = "No puede esta vacio")
        public String articleId;

        @SerializedName("quantity")
        @Min(value = 1, message = "Debe ser positivo")
        public int quantity;
    }

    public static OrderPlacedEvent fromJson(String json) {
        return GsonTools.gson().fromJson(json, OrderPlacedEvent.class);
    }
}