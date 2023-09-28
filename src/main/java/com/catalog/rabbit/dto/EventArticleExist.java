package com.catalog.rabbit.dto;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;

public class EventArticleExist {
    @NotEmpty(message = "No puede esta vacio")
    @SerializedName("articleId")
    public String articleId;
    @NotEmpty(message = "No puede esta vacio")
    @SerializedName("referenceId")
    public String referenceId;
    @SerializedName("valid")
    public boolean valid;

    public static EventArticleExist fromJson(String json) {
        return GsonTools.gson().fromJson(json, EventArticleExist.class);
    }
}
