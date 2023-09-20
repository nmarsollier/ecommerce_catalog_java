package com.catalog.article.vo;

import com.catalog.utils.gson.Builder;
import com.catalog.utils.gson.JsonSerializable;
import com.catalog.utils.validator.MaxLen;
import com.catalog.utils.validator.MinLen;
import com.catalog.utils.validator.MinValue;
import com.catalog.utils.validator.Required;
import com.google.gson.annotations.SerializedName;

/**
 * Objeto valor para crear un articulo nuevo.
 */
public class NewData implements JsonSerializable {
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

    @MinValue(0)
    @SerializedName("price")
    public double price;

    @SerializedName("stock")
    @MinValue(0)
    public int stock;

    public static NewData fromJson(String json) {
        return Builder.gson().fromJson(json, NewData.class);
    }

    @Override
    public String toJson() {
        return Builder.gson().toJson(this);
    }
}
