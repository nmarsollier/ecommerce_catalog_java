package com.catalog.security.dto;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    public final String id;
    @SerializedName("name")
    public final String name;
    @SerializedName("login")
    public final String login;
    @SerializedName("permissions")
    public final String[] permissions;

    public User(String id, String name, String login, String[] permissions) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.permissions = permissions;
    }

    public static User fromJson(String json) {
        return GsonTools.gson().fromJson(json, User.class);
    }
}