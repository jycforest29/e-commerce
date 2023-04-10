package com.jycforest29.commerce.common.config.security;

import com.jycforest29.commerce.common.aop.LoginAuthUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginAuthUserResolver());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
