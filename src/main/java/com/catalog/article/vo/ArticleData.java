package com.catalog.article.vo;

import com.catalog.utils.gson.Builder;
import com.catalog.utils.gson.JsonSerializable;
import com.catalog.utils.validator.MaxLen;
import com.catalog.utils.validator.MinLen;
import com.catalog.utils.validator.Required;
import com.google.gson.annotations.SerializedName;

/**
 * Objeto valor para artículos.
 */
public class ArticleData implements JsonSerializable {
    @SerializedName("_id")
    public String id;

    @SerializedName("name")
    @Required()
    @MinLen(1)
    @MaxLen(60)
    public String name;

    @SerializedName("description")
    @MaxLen(2048)
    public String description;

    @SerializedName("image")
    @MinLen(30)
    @MaxLen(40)
    public String image;

    @SerializedName("price")
    public double price;

    @SerializedName("stock")
    public int stock;

    @SerializedName("enabled")
    public boolean enabled = true;

    @Override
    public String toJson() {
        return Builder.gson().toJson(this);
    }
}