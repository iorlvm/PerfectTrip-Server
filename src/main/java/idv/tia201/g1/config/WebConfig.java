package idv.tia201.g1.config;

import idv.tia201.g1.authentication.filter.AdminLoginInterceptor;
import idv.tia201.g1.authentication.filter.CompanyLoginInterceptor;
import idv.tia201.g1.authentication.filter.TokenParsingInterceptor;
import idv.tia201.g1.authentication.filter.UserLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TokenParsingInterceptor tokenParsingInterceptor;
    private final UserLoginInterceptor userLoginInterceptor;
    private final CompanyLoginInterceptor companyLoginInterceptor;
    private final AdminLoginInterceptor adminLoginInterceptor;

    public WebConfig(TokenParsingInterceptor tokenParsingInterceptor, UserLoginInterceptor userLoginInterceptor, CompanyLoginInterceptor companyLoginInterceptor, AdminLoginInterceptor adminLoginInterceptor) {
        this.tokenParsingInterceptor = tokenParsingInterceptor;
        this.userLoginInterceptor = userLoginInterceptor;
        this.companyLoginInterceptor = companyLoginInterceptor;
        this.adminLoginInterceptor = adminLoginInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 跨域請求設定 未來確定規格後要修正
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 解析token的攔截器
        registry.addInterceptor(tokenParsingInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/user/**");   // TODO: 討論後修改

        registry.addInterceptor(companyLoginInterceptor)
                .addPathPatterns("/company/**");   // TODO: 討論後修改

        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/admin/**");  // TODO: 討論後修改
    }
}
