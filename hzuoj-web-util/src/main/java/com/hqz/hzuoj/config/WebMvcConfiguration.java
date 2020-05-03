package com.hqz.hzuoj.config;

import com.hqz.hzuoj.interceptors.AdminInterceptor;
import com.hqz.hzuoj.interceptors.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;
/**
 * 拦截器配置类
 */
@Configuration
public class WebMvcConfiguration  implements WebMvcConfigurer {

    @Autowired
    AdminInterceptor adminInterceptor;

    @Autowired
    UserInterceptor userInterceptor;

    @Override

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor).addPathPatterns("/**").excludePathPatterns("/error", "/static/**");
        registry.addInterceptor(userInterceptor).addPathPatterns("/**").excludePathPatterns("/error", "/static/**");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
    }

}
