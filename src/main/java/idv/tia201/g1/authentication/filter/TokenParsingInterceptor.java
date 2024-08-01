package idv.tia201.g1.authentication.filter;

import idv.tia201.g1.authentication.service.TokenService;
import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenParsingInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;

    public TokenParsingInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            UserAuth userAuth = tokenService.validateToken(token);
            if (userAuth != null) {
                UserHolder.saveUser(userAuth);
                tokenService.flashLoginExpire(token);
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 使用完畢的時候移除資料
        UserHolder.removeUser();
    }
}