package com.catalog.security;

import com.catalog.utils.gson.Builder;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("login")
    public String login;
    @SerializedName("permissions")
    public String[] permissions;

    public static User fromJson(String json) {
        return Builder.gson().fromJson(json, User.class);
    }
}