package com.catalog.rabbit.dto;

import com.catalog.utils.gson.Builder;
import com.catalog.utils.validator.Required;
import com.google.gson.annotations.SerializedName;

public class OrderPlacedEvent {
    @SerializedName("orderId")
    public String orderId;
    @SerializedName("cartId")
    public String cartId;
    @SerializedName("articles")
    public Article[] articles;

    public static class Article {
        @SerializedName("articleId")
        @Required
        public String articleId;

        @SerializedName("quantity")
        @Required
        public int quantity;
    }

    public static OrderPlacedEvent fromJson(String json) {
        return Builder.gson().fromJson(json, OrderPlacedEvent.class);
    }
}