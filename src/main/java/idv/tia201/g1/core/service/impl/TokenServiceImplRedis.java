package idv.tia201.g1.core.service.impl;

import com.google.gson.Gson;
import idv.tia201.g1.core.entity.AuthInfo;
import idv.tia201.g1.core.service.TokenService;
import idv.tia201.g1.core.entity.UserAuth;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static idv.tia201.g1.core.utils.Constants.LOGIN_TTL;
import static idv.tia201.g1.core.utils.Constants.LOGIN_USER;

@Service
public class TokenServiceImplRedis implements TokenService {
    private final Gson gson = new Gson();
    private final StringRedisTemplate stringRedisTemplate;

    public TokenServiceImplRedis(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public <T extends UserAuth> String createToken(T user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = LOGIN_USER + token;

        AuthInfo authInfo = new AuthInfo();
        authInfo.setId(user.getId());
        authInfo.setRole(user.getRole());

        String json = gson.toJson(authInfo);
        stringRedisTemplate.opsForValue().set(key, json, LOGIN_TTL, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public UserAuth validateToken(String token) {
        String key = LOGIN_USER + token;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            return gson.fromJson(json, AuthInfo.class);
        } else {
            return null;
        }
    }

    @Override
    public void revokeToken(String token) {
        String key = LOGIN_USER + token;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void flashLoginExpire(String token) {
        String key = LOGIN_USER + token;
        stringRedisTemplate.expire(key, LOGIN_TTL, TimeUnit.SECONDS);
    }
}
