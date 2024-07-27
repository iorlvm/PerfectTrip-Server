package idv.tia201.g1.authentication.service;

public interface TokenService {
    <T extends UserAuth> String setToken(T user);

    UserAuth validateToken(String token);

    void flashLoginExpire(String token);
}
