package com.catalog.rabbit.dto;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public final class ConsumedOrderPlacedEvent {
    @SerializedName("orderId")
    public final String orderId;
    @SerializedName("cartId")
    public final String cartId;
    @SerializedName("articles")
    public final Article[] articles;

    public ConsumedOrderPlacedEvent(
            String orderId,
            String cartId,
            Article[] articles
    ) {
        this.orderId = orderId;
        this.cartId = cartId;
        this.articles = articles;
    }

    public static final class Article {
        @SerializedName("articleId")
        @NotEmpty(message = "No puede esta vacio")
        public final String articleId;

        @SerializedName("quantity")
        @Min(value = 1, message = "Debe ser positivo")
        public final int quantity;

        public Article(
                String articleId,
                int quantity
        ) {
            this.articleId = articleId;
            this.quantity = quantity;
        }
    }

    public static ConsumedOrderPlacedEvent fromJson(String json) {
        return GsonTools.gson().fromJson(json, ConsumedOrderPlacedEvent.class);
    }
}