package com.iecube.ota.config;

import com.iecube.ota.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoginInterceptorConfigurer implements WebMvcConfigurer {
    private AuthInterceptor authInterceptor;

    @Autowired
    public void InterceptorConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器注册
        //配置白名单： List集合
        // addPathPatterns("表示要拦截的url是什么").excludePathPatterns("list集合 表示白名单")
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(authInterceptor.WHITE_LIST);
    }
}
