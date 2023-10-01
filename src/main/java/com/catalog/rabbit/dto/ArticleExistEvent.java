package com.catalog.rabbit.dto;

import com.catalog.rabbit.PublishArticleValidation;
import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;

public final class ArticleExistEvent {
    @NotEmpty(message = "No puede esta vacio")
    @SerializedName("articleId")
    public final String articleId;
    @NotEmpty(message = "No puede esta vacio")
    @SerializedName("referenceId")
    public final String referenceId;
    @SerializedName("valid")
    public final boolean valid;

    public ArticleExistEvent(
            String articleId,
            String referenceId,
            boolean valid
    ) {
        this.articleId = articleId;
        this.referenceId = referenceId;
        this.valid = valid;
    }


    public static ArticleExistEvent fromEventArticleExist(ArticleExistEvent event, boolean valid) {
        return new ArticleExistEvent(
                event.articleId,
                event.referenceId,
                valid
        );
    }

    public static ArticleExistEvent fromJson(String json) {
        return GsonTools.gson().fromJson(json, ArticleExistEvent.class);
    }

    public void publishIn(
            PublishArticleValidation publishArticleValidation,
            String exchange,
            String queue
    ) {
        publishArticleValidation.publish(exchange, queue, this);
    }
}
