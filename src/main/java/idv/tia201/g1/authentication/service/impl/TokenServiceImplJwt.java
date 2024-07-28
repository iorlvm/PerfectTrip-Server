package idv.tia201.g1.authentication.service.impl;

import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.authentication.service.UserAuth;

public class TokenServiceImplJwt implements TokenService {
    @Override
    public <T extends UserAuth> String createToken(T user) {
        return null;
    }

    @Override
    public UserAuth validateToken(String token) {
        return null;
    }

    @Override
    public void flashLoginExpire(String token) {

    }
}
