package com.wangshanhai.power.examples.api;

import com.wangshanhai.power.annotation.RequestNotNeedAuth;
import com.wangshanhai.power.annotation.RequiresPermissions;
import com.wangshanhai.power.dto.TokenInfo;
import com.wangshanhai.power.open.ShanhaiPower;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class LoginController {
    /**
     * 用户登录
     * @return
     */
    @RequestNotNeedAuth
    @GetMapping("/login")
    public TokenInfo login(){
        return ShanhaiPower.login("zhangsan");
    }
    /**
     * 用户登录
     * @return
     */
    @RequestNotNeedAuth
    @GetMapping("/login2")
    public TokenInfo login2(){
        return ShanhaiPower.login("zhangsan");
    }

    /**
     * 用户登录
     * @return
     */
    @RequiresPermissions("user:details")
    @GetMapping("/queryUserInfo")
    public TokenInfo queryUserInfo(){
        return ShanhaiPower.getCurrentUserToken();
    }
}
