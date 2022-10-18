package com.wangshanhai.power.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangshanhai.power.service.PowerStoreService;
import com.wangshanhai.power.service.TokenGenerateService;
import com.wangshanhai.power.service.impl.PowerTokenGenerateServiceImpl;
import com.wangshanhai.power.service.impl.RedisPowerStoreServiceImpl;
import com.wangshanhai.power.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 * @author Shmily
 */
@Configuration
@EnableConfigurationProperties(ShanhaiPowerConfig.class)
@AutoConfigureAfter(WebMvcConfigurationSupport.class)
public class ShanHaiPowerConfigurer  implements WebMvcConfigurer {
    @Autowired
    private ShanhaiPowerConfig shanhaiPowerConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Logger.info("[ShanHaiPower-Init]-init Component");
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "spring.redis",
            name = "host"
    )
    @ConditionalOnMissingBean(PowerStoreService.class)
    @SuppressWarnings("all")
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
        Logger.info("[ShanHaiPower-Init]-DefaultRedisCacheService创建成功!");
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public PowerStoreService generateDefaultPowerStoreService(RedisTemplate<String, Object> redisTemplate ) {
        return new RedisPowerStoreServiceImpl(redisTemplate);
    };


    @Bean
    @ConditionalOnMissingBean
    public TokenGenerateService generateDefaultTokenGenerateService() {
        return new PowerTokenGenerateServiceImpl();
    };
}
