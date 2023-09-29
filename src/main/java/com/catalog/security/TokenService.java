package com.catalog.security;

import com.catalog.security.dto.User;
import com.catalog.utils.errors.UnauthorizedError;
import com.catalog.utils.expiringMap.ExpiringMap;
import io.micrometer.common.util.StringUtils;
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
    TokenDao tokenDao;

    static final ExpiringMap<String, User> map = new ExpiringMap<>(60 * 60, 60 * 5);

    public void validateAdmin(String token) {
        validateLoggedIn(token);
        User cachedUser = map.get(token);
        if (cachedUser == null) {
            throw new UnauthorizedError();
        }
        if (!contains(cachedUser.permissions, "admin")) {
            throw new UnauthorizedError();
        }
    }

    public void validateLoggedIn(String token) {
        if (StringUtils.isBlank(token)) {
            throw new UnauthorizedError();
        }

        User cachedUser = map.get(token);
        if (cachedUser != null) {
            return;
        }

        User user = tokenDao.retrieveUser(token);
        if (user == null) {
            throw new UnauthorizedError();
        }
        map.put(token, user);
    }

    public void invalidate(String token) {
        map.remove(token);
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
