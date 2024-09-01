package idv.tia201.g1.core.aspect;

import idv.tia201.g1.core.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static idv.tia201.g1.core.utils.Constants.ROLE_ADMIN;

@Component
@Aspect
public class AdminLoginAspect {

    @Around("execution(String idv.tia201.g1.core.controller.RouterController.*(org.springframework.ui.Model))")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!ROLE_ADMIN.equals(UserHolder.getRole())) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            return new StringBuilder()
                    .append("redirect:")
                    .append(request.getScheme())
                    .append("://")
                    .append(request.getServerName())
                    .append(":")
                    .append(request.getServerPort())
                    .append("/login")
                    .toString();
        }

        return joinPoint.proceed();
    }
}
