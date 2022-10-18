package com.wangshanhai.power.examples.service;

import com.wangshanhai.power.service.PowerStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于原生Redis协议
 * @author Shmily
 */
@Service
public class DemoPowerStoreServiceImpl implements PowerStoreService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public Long expire(String key, int time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return 1L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Object get(String key) {
        if(StringUtils.isEmpty(key)){
            return null;
        }
        Object tmp= redisTemplate.opsForValue().get(key);
        if(tmp==null){
            return null;
        }
        return tmp;
    }

    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
