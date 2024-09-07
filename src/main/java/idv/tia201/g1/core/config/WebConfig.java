package idv.tia201.g1.core.config;

import idv.tia201.g1.core.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TokenParsingInterceptor tokenParsingInterceptor;

    public WebConfig(TokenParsingInterceptor tokenParsingInterceptor) {
        this.tokenParsingInterceptor = tokenParsingInterceptor;
    }

    private CorsConfiguration corsConfig() {
        // 跨域請求設定 未來確定規格後要修正
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("http://localhost:5173");  // TODO: 上線後要修改
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        // Spring Boot的跨域請求 (寫法不一樣)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 解析token的攔截器
        registry.addInterceptor(tokenParsingInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/chat/**");

        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/user/**");   // TODO: 討論後修改

        registry.addInterceptor(new CompanyLoginInterceptor())
                .addPathPatterns("/company/**")
                .excludePathPatterns("/company/login", "/company/register");    // TODO: 討論後修改

        registry.addInterceptor(new AdminLoginInterceptor())
                .addPathPatterns("/admin/**")  // TODO: 討論後修改
                .excludePathPatterns("/admin/login");
    }
}
