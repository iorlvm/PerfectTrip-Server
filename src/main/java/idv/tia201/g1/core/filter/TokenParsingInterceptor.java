package idv.tia201.g1.core.filter;

import idv.tia201.g1.core.service.TokenService;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static idv.tia201.g1.core.utils.Constants.*;

@Component
public class TokenParsingInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;

    public TokenParsingInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 從請求頭中取出token
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            UserAuth userAuth = tokenService.validateToken(token);
            if (userAuth != null) {
                UserHolder.saveUser(userAuth);
                // TODO: 未來刪除 測試用token 避免過期
                if (ROLE_ADMIN.equals(token) || ROLE_COMPANY.equals(token) || "customer".equals(token)) {
                    return true;
                }
                tokenService.flashLoginExpire(token);
            }
            return true;
        }

        // 沒有攜帶請求頭的情況, 嘗試從session中取出token
        HttpSession session = request.getSession(false);
        if (session != null) {
            String token = (String) session.getAttribute("token");
            UserAuth userAuth = tokenService.validateToken(token);
            if (userAuth != null) {
                UserHolder.saveUser(userAuth);
                // TODO: 未來刪除 測試用token 避免過期
                if (ROLE_ADMIN.equals(token)) {
                    return true;
                }
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