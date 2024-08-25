package idv.tia201.g1.core.service;

import idv.tia201.g1.core.entity.UserAuth;

public interface TokenService {
    <T extends UserAuth> String createToken(T user);

    UserAuth validateToken(String token);

    void flashLoginExpire(String token);
}
