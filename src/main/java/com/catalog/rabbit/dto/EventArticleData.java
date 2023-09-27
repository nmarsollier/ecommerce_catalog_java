package com.catalog.rabbit.dto;

import com.catalog.utils.gson.Builder;
import com.catalog.utils.gson.JsonSerializable;
import com.catalog.utils.validator.Required;
import com.google.gson.annotations.SerializedName;

public class EventArticleData implements JsonSerializable {
    @Required
    @SerializedName("articleId")
    public String articleId;
    @Required
    @SerializedName("referenceId")
    public String referenceId;
    @SerializedName("valid")
    public boolean valid;
    @SerializedName("price")
    public double price;
    @SerializedName("stock")
    public int stock;

    public static EventArticleExist fromJson(String json) {
        return Builder.gson().fromJson(json, EventArticleExist.class);
    }

    @Override
    public String toJson() {
        return Builder.gson().toJson(this);
    }
}