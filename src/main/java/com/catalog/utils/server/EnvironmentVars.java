package com.catalog.utils.server;

import com.google.gson.annotations.SerializedName;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentVars {
    public final EnvData envData;

    {
        envData = new EnvData();

        String authService = System.getenv("AUTH_SERVICE_URL");
        if (authService != null && !authService.isEmpty()) {
            envData.securityServerUrl = authService;
        }
        String rabbitUrl = System.getenv("RABBIT_URL");
        if (rabbitUrl != null && !rabbitUrl.isEmpty()) {
            envData.rabbitServerUrl = rabbitUrl;
        }
        String mongoUrl = System.getenv("MONGO_URL");
        if (mongoUrl != null && !mongoUrl.isEmpty()) {
            envData.databaseUrl = mongoUrl;
        }
        String wwwPath = System.getenv("WWW_PATH");
        if (wwwPath != null && !wwwPath.isEmpty()) {
            envData.staticLocation = wwwPath;
        }
    }

    public static class EnvData {
        @SerializedName("securityServerUrl")
        public String securityServerUrl = "http://localhost:3000";
        @SerializedName("rabbitServerUrl")
        public String rabbitServerUrl = "localhost";
        @SerializedName("databaseUrl")
        public String databaseUrl = "localhost";
        @SerializedName("staticLocation")
        public String staticLocation = "www";
    }
}