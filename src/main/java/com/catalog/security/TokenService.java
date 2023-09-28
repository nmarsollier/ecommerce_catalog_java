package com.catalog.security;

import com.catalog.utils.errors.UnauthorizedError;
import com.catalog.utils.server.EnvironmentVars;
import io.micrometer.common.util.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @apiDefine AuthHeader
 * @apiExample {String} Header Autorización
 * Authorization=bearer {token}
 * @apiErrorExample 401 Unauthorized
 * HTTP/1.1 401 Unauthorized
 */
@Service
public class TokenService {
    @Autowired
    EnvironmentVars environmentVars;

    static final ExpiringMap<String, User> map = new ExpiringMap<>(60 * 60, 60 * 5);

    public void validateAdmin(String token) {
        validate(token);
        User cachedUser = map.get(token);
        if (cachedUser == null) {
            throw new UnauthorizedError();
        }
        if (!contains(cachedUser.permissions, "admin")) {
            throw new UnauthorizedError();
        }
    }

    public void validate(String token) {
        if (StringUtils.isBlank(token)) {
            throw new UnauthorizedError();
        }

        User cachedUser = map.get(token);
        if (cachedUser != null) {
            return;
        }

        User user = retrieveUser(token);
        if (user == null) {
            throw new UnauthorizedError();
        }
        map.put(token, user);
    }

    public void invalidate(String token) {
        map.remove(token);
    }

    private User retrieveUser(String token) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(environmentVars.envData.securityServerUrl + "/v1/users/current");
        request.addHeader("Authorization", token);
        HttpResponse response;
        try {
            response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity == null) {
                return null;
            }
            String body = EntityUtils.toString(responseEntity);
            return User.fromJson(body);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean contains(String[] permissions, String permission) {
        for (String s : permissions) {
            if (s.equals(permission)) {
                return true;
            }
        }
        return false;
    }
}
