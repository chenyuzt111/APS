package com.benewake.system.config;

import com.benewake.system.controller.intercepter.LoginStatusInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author Lcs
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer  {



    @Autowired
    private LoginStatusInterceptor loginStatusInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 这里的顺序决定了拦截器的执行顺序
        registry.addInterceptor(loginStatusInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }

}
