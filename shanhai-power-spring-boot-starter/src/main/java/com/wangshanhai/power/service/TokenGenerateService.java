package com.wangshanhai.power.service;

import com.wangshanhai.power.config.ShanhaiPowerConfig;

import java.util.Map;

/**
 * Token生成服务
 * @author Shmily
 */
public interface TokenGenerateService {
    /**
     * 生成Token
     * @param shanhaiPowerConfig
     * @param extParams 自定义参数
     * @return
     */
    public String generateToken(ShanhaiPowerConfig shanhaiPowerConfig, Map<String, Object> extParams);
}
