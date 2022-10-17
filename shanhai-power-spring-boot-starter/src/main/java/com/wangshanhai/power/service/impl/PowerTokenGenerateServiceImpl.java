package com.wangshanhai.power.service.impl;

import com.wangshanhai.power.config.ShanhaiPowerConfig;
import com.wangshanhai.power.service.TokenGenerateService;
import com.wangshanhai.power.utils.TokenAlgorithmUtils;

import java.util.Map;
import java.util.UUID;

/**
 * 生成Token
 * @author Shmily
 */
public class PowerTokenGenerateServiceImpl implements TokenGenerateService {
    @Override
    public String generateToken(ShanhaiPowerConfig shanhaiPowerConfig, Map<String, Object> extParams) {
        if(shanhaiPowerConfig!=null){
            switch (shanhaiPowerConfig.getTokenAlgorithm()){
                case "uuid": return TokenAlgorithmUtils.uuid();
                case "sha512": return TokenAlgorithmUtils.SHA512(TokenAlgorithmUtils.uuid());
                default: return UUID.randomUUID().toString();
            }
        }
        return null;
    }


}

