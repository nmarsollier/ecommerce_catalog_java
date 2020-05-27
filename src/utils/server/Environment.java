package utils.server;

public class Environment {
    static EnvData envData = new EnvData();

    public static EnvData getEnv() {
        return envData;
    }

    static {
        envData = new EnvData();

        String port = System.getenv("SERVER_PORT");
        if (port != null && port.length() > 0 && Integer.parseInt(port) != 0) {
            envData.serverPort = Integer.parseInt(port);
        }
        String authService = System.getenv("AUTH_SERVICE_URL");
        if (authService != null && authService.length() > 0) {
            envData.securityServerUrl = authService;
        }
        String rabbitUrl = System.getenv("RABBIT_URL");
        if (rabbitUrl != null && rabbitUrl.length() > 0) {
            envData.rabbitServerUrl = rabbitUrl;
        }
        String mongoUrl = System.getenv("MONGO_URL");
        if (mongoUrl != null && mongoUrl.length() > 0) {
            envData.databaseUrl = mongoUrl;
        }
        String wwwPath = System.getenv("WWW_PATH");
        if (wwwPath != null && wwwPath.length() > 0) {
            envData.staticLocation = wwwPath;
        }
    }
}