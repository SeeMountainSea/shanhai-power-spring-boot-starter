package com.wangshanhai.power.config;

import com.wangshanhai.power.annotation.RequestNotNeedAuth;
import com.wangshanhai.power.exceptions.ShanHaiNotLoginException;
import com.wangshanhai.power.open.ShanhaiPower;
import com.wangshanhai.power.utils.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 鉴权拦截器
 * @author Shmily
 */
public class ShanhaiPowerInterceptor   extends HandlerInterceptorAdapter {
    /**
     * 用户会话有效性校验
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ShanhaiPowerConfig shanhaiPowerConfig= ShanhaiPower.getConfig();
        if(!(handler instanceof  HandlerMethod)){
           return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequestNotNeedAuth requestNotNeedAuth=handlerMethod.getMethodAnnotation(RequestNotNeedAuth.class);
        //如果方法无需鉴权，则直接放行
        if(requestNotNeedAuth!=null){
            return true;
        }
        String token=request.getHeader(shanhaiPowerConfig.getTokenName());
        //进入该拦截器的方法必须带token
        if(StringUtils.isEmpty(token)){
            Logger.error("[ShanhaiPower-Token-NotFind]-token:{}", token);
            throw  new ShanHaiNotLoginException("非法访问");
        }else{
            int status=  ShanhaiPower.checkToken(request);
            //会话有效
            if(status==1){
                ShanhaiPower.refreshTokenAccessTime(request);
                return true;
            }else{ //会话无效
                if(status==-1){
                    throw   new ShanHaiNotLoginException("10002","登录超时");
                }
                if(status==-2){
                    throw  new ShanHaiNotLoginException("10003","你的账号正在其他渠道登录，请注意口令安全");
                }
            }
        }
        return false;
    }



}
