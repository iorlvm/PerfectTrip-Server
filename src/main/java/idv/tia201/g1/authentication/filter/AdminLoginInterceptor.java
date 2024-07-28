package idv.tia201.g1.authentication.filter;

import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


import static idv.tia201.g1.utils.Constants.ROLE_ADMIN;

public class AdminLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserAuth userAuth = UserHolder.getUser();
        if (userAuth != null && ROLE_ADMIN.equals(userAuth.getRole())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
