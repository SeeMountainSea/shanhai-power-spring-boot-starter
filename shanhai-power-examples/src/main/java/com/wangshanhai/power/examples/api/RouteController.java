package com.wangshanhai.power.examples.api;

import com.wangshanhai.power.config.ShanhaiPowerConfig;
import com.wangshanhai.power.dto.TokenInfo;
import com.wangshanhai.power.open.ShanhaiPower;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route")
public class RouteController {
    /**
     * 用户登录
     * @return
     */
    @GetMapping("/queryUserInfo")
    public TokenInfo queryUserInfo(){
        ShanhaiPower.setTokenSessionData("qdyy",ShanhaiPower.getConfig());
        return ShanhaiPower.getCurrentUserToken();
    }
    /**
     * 用户登录
     * @return
     */
    @GetMapping("/queryConfig")
    public ShanhaiPowerConfig queryConfig(){
        return (ShanhaiPowerConfig) ShanhaiPower.getTokenSessionData("qdyy");
    }
}
