package idv.tia201.g1.core.filter;

import idv.tia201.g1.core.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


import static idv.tia201.g1.core.utils.Constants.ROLE_COMPANY;

public class CompanyLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (ROLE_COMPANY.equals(UserHolder.getRole())) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
