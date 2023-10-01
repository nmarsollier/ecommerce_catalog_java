package com.catalog.rabbit.dto;

import com.catalog.article.dto.ArticleData;
import com.catalog.rabbit.PublishArticleData;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;

public final class PublishArticleDataEvent {
    @NotEmpty(message = "No puede esta vacio")
    @SerializedName("articleId")
    public final String articleId;

    @NotEmpty(message = "No puede esta vacio")
    @SerializedName("referenceId")
    public final String referenceId;

    @SerializedName("valid")
    public final boolean valid;

    @SerializedName("price")
    public final double price;

    @SerializedName("stock")
    public final int stock;

    public PublishArticleDataEvent(
            String articleId,
            String referenceId,
            boolean valid,
            double price,
            int stock
    ) {
        this.articleId = articleId;
        this.referenceId = referenceId;
        this.valid = valid;
        this.price = price;
        this.stock = stock;
    }

    public static PublishArticleDataEvent fromArticleData(ArticleData article, String referenceId) {
        return new PublishArticleDataEvent(
                article.id,
                referenceId,
                article.enabled,
                article.price,
                article.stock
        );
    }

    public static PublishArticleDataEvent fromEventArticleExist(ArticleExistEvent exist) {
        return new PublishArticleDataEvent(
                exist.articleId,
                exist.referenceId,
                false,
                0,
                0
        );
    }

    public static PublishArticleDataEvent fromOrderPlacedEvent(
            ConsumedOrderPlacedEvent.Article exist,
            String referenceId
    ) {
        return new PublishArticleDataEvent(
                exist.articleId,
                referenceId,
                false,
                0,
                0
        );
    }

    public void publishIn(PublishArticleData publishArticleData, String exchange, String queue) {
        publishArticleData.publish(exchange, queue, this);
    }
}