package com.wangshanhai.power.examples.config;

import com.wangshanhai.power.annotation.EnableShanHaiPower;
import com.wangshanhai.power.config.ShanhaiPowerInterceptor;
import com.wangshanhai.power.config.ShanhaiPowerAnnotationPermissionsInterceptor;
import com.wangshanhai.power.config.ShanhaiPowerRoutePermissionsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Shmily
 */
@Configuration
@EnableShanHaiPower
public class ShanhaiConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShanhaiPowerInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new ShanhaiPowerAnnotationPermissionsInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new ShanhaiPowerRoutePermissionsInterceptor()).addPathPatterns("/**");
    }
}
