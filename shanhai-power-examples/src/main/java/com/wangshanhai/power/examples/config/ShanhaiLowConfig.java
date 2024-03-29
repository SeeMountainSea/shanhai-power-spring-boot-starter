package com.wangshanhai.power.examples.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangshanhai.power.config.ShanhaiPowerAnnotationPermissionsInterceptor;
import com.wangshanhai.power.config.ShanhaiPowerInterceptor;
import com.wangshanhai.power.config.ShanhaiPowerRoutePermissionsInterceptor;
import com.wangshanhai.power.utils.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 基于SpringBoot1.5.x的配置
 * @author Shmily
 */
//@Configuration
//@EnableShanHaiPower
//@EnableConfigurationProperties(ShanhaiPowerConfig.class)
//@AutoConfigureAfter(WebMvcConfigurationSupport.class)
public class ShanhaiLowConfig extends WebMvcConfigurationSupport {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShanhaiPowerInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new ShanhaiPowerAnnotationPermissionsInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new ShanhaiPowerRoutePermissionsInterceptor()).addPathPatterns("/**");
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        Logger.info("DemoRedisCacheService创建成功!", new Object[0]);
        return template;
    }
}
