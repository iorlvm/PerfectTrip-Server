package idv.tia201.g1.config;

import idv.tia201.g1.authentication.filter.AdminLoginInterceptor;
import idv.tia201.g1.authentication.filter.CompanyLoginInterceptor;
import idv.tia201.g1.authentication.filter.TokenParsingInterceptor;
import idv.tia201.g1.authentication.filter.UserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private TokenParsingInterceptor tokenParsingInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 靜態資源配置  之後要修改路徑
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
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

        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/user/**");   // TODO: 討論後修改

        registry.addInterceptor(new CompanyLoginInterceptor())
                .addPathPatterns("/company/**");   // TODO: 討論後修改

        registry.addInterceptor(new AdminLoginInterceptor())
                .addPathPatterns("/admin/**");  // TODO: 討論後修改
    }
}
