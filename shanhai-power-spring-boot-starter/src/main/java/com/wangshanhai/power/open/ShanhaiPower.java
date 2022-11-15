package com.wangshanhai.power.open;

import com.wangshanhai.power.config.ShanhaiPowerConfig;
import com.wangshanhai.power.dto.TokenInfo;
import com.wangshanhai.power.exceptions.ShanHaiNotLoginException;
import com.wangshanhai.power.service.PermissionService;
import com.wangshanhai.power.service.PowerStoreService;
import com.wangshanhai.power.service.TokenGenerateService;
import com.wangshanhai.power.utils.HttpContextUtils;
import com.wangshanhai.power.utils.Logger;
import com.wangshanhai.power.utils.SpringBeanUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 山海Power核心类
 * @author Shmily
 */
public class ShanhaiPower {
    private static ShanhaiPowerConfig shanhaiPowerConfig;
    private static PowerStoreService powerStoreService;
    private static TokenGenerateService tokenGenerateService;
    private static PermissionService permissionService;
    /**
     * 登录
     * @param userFlag 用户标识
     * @return
     */
    public static TokenInfo login(Object userFlag){
        return login(userFlag,"Default",new HashMap<>());
    }
    /**
     * 登录
     * @param userFlag 用户标识
     * @param channel 登陆渠道
     * @return
     */
    public static TokenInfo login(Object userFlag,String channel){
        return login(userFlag,channel,new HashMap<>());
    }
    /**
     * 登录
     * @param userFlag 用户标识
     * @param channel  渠道标识
     * @param extParams 自定义会话参数
     * @return
     */
    public static TokenInfo login(Object userFlag,String channel, Map<String, Object> extParams){
        if(StringUtils.isEmpty(channel)){
            channel="Default";
        }
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        TokenInfo tokenInfoDTO= TokenInfo.builder()
                .lastAccessTime(new Date())
                .status(1)
                .maxActiveTime(shanhaiPowerConfig.getMaxActiveTime())
                .userFlag(userFlag)
                .token(generateToken(shanhaiPowerConfig,extParams))
                .loginChannel(channel)
                .build();
        String tokenInfoFlag="shanhaipower:"+String.valueOf(userFlag)+":"+channel;
        String tokenLoginListFlag="shanhaipower:"+String.valueOf(userFlag)+":"+channel+":LoginList";
        String tokenFlag="shanhaipower:"+tokenInfoDTO.getToken();
        if(shanhaiPowerConfig.getExclusiveLogin()&&powerStoreService.exists(tokenInfoFlag)){
            String lastToken=String.valueOf(powerStoreService.get(tokenInfoFlag));
            String lastTokenFlag="shanhaipower:"+lastToken;
            TokenInfo tokenInfo=powerStoreService.get(lastTokenFlag)==null?null:(TokenInfo)powerStoreService.get(lastTokenFlag);
            if(tokenInfo!=null){
                tokenInfo.setStatus(-2);
                powerStoreService.set(lastTokenFlag,tokenInfo);
            }
        }
        powerStoreService.set(tokenFlag,tokenInfoDTO, shanhaiPowerConfig.getTokenTimeout());
        powerStoreService.set(tokenInfoFlag,tokenInfoDTO.getToken(), shanhaiPowerConfig.getTokenTimeout());
        if(powerStoreService.exists(tokenLoginListFlag)){
            List<String> tokenLoginListT=(List)powerStoreService.get(tokenLoginListFlag);
            tokenLoginListT.add(tokenInfoDTO.getToken());
            powerStoreService.set(tokenLoginListFlag,tokenLoginListT, powerStoreService.ttl(tokenLoginListFlag));
        }else{
            List<String> tokenLoginListT=new ArrayList<>();
            tokenLoginListT.add(tokenInfoDTO.getToken());
            powerStoreService.set(tokenLoginListFlag,tokenLoginListT,shanhaiPowerConfig.getTokenTimeout());
        }
        return tokenInfoDTO;
    }

    /**
     * 注销指定Token
     * @param token
     * @return
     */
    public static void logOutByToken(String token){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token;
        powerStoreService.del(tokenFlag);
    }
    /**
     * 注销指定用户会话
     * @param userFlag
     * @return
     */
    public static void logOut(Object userFlag){
        logOut(userFlag,"Default");
    }
    /**
     * 注销指定用户指定渠道会话
     * @param userFlag
     * @param channel
     * @return
     */
    public static void logOut(Object userFlag,String channel){
        PowerStoreService powerStoreService= loadCacheService();
        String tokenInfoFlag="shanhaipower:"+String.valueOf(userFlag)+":"+channel;
        String tokenLoginListFlag="shanhaipower:"+String.valueOf(userFlag)+":"+channel+":LoginList";
        if(powerStoreService.exists(tokenLoginListFlag)){
            List<String> tokenLoginListT=(List)powerStoreService.get(tokenLoginListFlag);
            for(String t:tokenLoginListT){
                String tokenFlag="shanhaipower:"+t;
                powerStoreService.del(tokenFlag);
            }
        }
        powerStoreService.del(tokenInfoFlag);
        powerStoreService.del(tokenLoginListFlag);
    }
    /**
     * 查询当前登录用户标识
     * @return
     */
    public static TokenInfo getCurrentUserToken(){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        String token= HttpContextUtils.getHttpServletRequest().getHeader(shanhaiPowerConfig.getTokenName());
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token;
        return powerStoreService.get(tokenFlag)==null?null:(TokenInfo)powerStoreService.get(tokenFlag);
    }
    /**
     * 查询当前登录用户标识
     * @return
     */
    public static TokenInfo getTokenInfo(String token){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token;
        return powerStoreService.get(tokenFlag)==null?null:(TokenInfo)powerStoreService.get(tokenFlag);
    }
    /**
     * 设置Token级会话数据
     * @return
     */
    public static void setTokenSessionData(String key,Object data){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        String token= HttpContextUtils.getHttpServletRequest().getHeader(shanhaiPowerConfig.getTokenName());
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token+":session:"+key;
        if(!powerStoreService.exists(tokenFlag)){
            powerStoreService.set(tokenFlag,data,shanhaiPowerConfig.getTokenSessionTimeout());
        }else{
            powerStoreService.set(tokenFlag,data,powerStoreService.ttl(tokenFlag));
        }
    }
    /**
     * 获取Token级会话数据
     * @return
     */
    public static Object getTokenSessionData(String key){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        String token= HttpContextUtils.getHttpServletRequest().getHeader(shanhaiPowerConfig.getTokenName());
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token+":session:"+key;
        return powerStoreService.get(tokenFlag);
    }
    /**
     * 校验token有效性
     * @return
     */
    public static Integer checkToken(HttpServletRequest request){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        String token= request.getHeader(shanhaiPowerConfig.getTokenName());
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token;
        TokenInfo tokenInfo=powerStoreService.get(tokenFlag)==null?null:(TokenInfo)powerStoreService.get(tokenFlag);
        if(tokenInfo!=null){
            String tokenInfoFlag="shanhaipower:"+tokenInfo.getUserFlag()+":"+tokenInfo.getLoginChannel();
            //同端异地登陆
            if(tokenInfo.getStatus()==-2){
                Logger.error("[ShanhaiPower-Token-OtherLogin]-token:{}", token);
                powerStoreService.del(tokenFlag);
                powerStoreService.del(tokenInfoFlag);
                return tokenInfo.getStatus();
            }else{
                long currentDate=System.currentTimeMillis();
                if((int)((currentDate-tokenInfo.getLastAccessTime().getTime())/1000L)<=tokenInfo.getMaxActiveTime()){
                    //会话有效
                    return 1;
                }else{
                    powerStoreService.del(tokenFlag);
                    powerStoreService.del(tokenInfoFlag);
                }
            }
        }
        Logger.error("[ShanhaiPower-Token-TimeOut]-token:{}", token);
        //会话不存在
        return -1;
    }

    /**
     * 刷新Token有效期
     */
    public static void refreshTokenAccessTime(HttpServletRequest request){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        String token= request.getHeader(shanhaiPowerConfig.getTokenName());
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token;
        TokenInfo tokenInfo=powerStoreService.get(tokenFlag)==null?null:(TokenInfo)powerStoreService.get(tokenFlag);
        if(tokenInfo!=null){
            tokenInfo.setLastAccessTime(new Date());
            long ttl=powerStoreService.ttl(tokenFlag);
            powerStoreService.set(tokenFlag,tokenInfo, ttl);
        }else{
            throw new ShanHaiNotLoginException("Token获取失败！");
        }
    }

    /**
     * 获取全局配置参数
     * @return
     */
    public static ShanhaiPowerConfig getConfig(){
        if(shanhaiPowerConfig==null){
            Logger.info("[ShanhaiPower-InitConfig]");
            shanhaiPowerConfig=SpringBeanUtils.getBean(ShanhaiPowerConfig.class);
        }
        return shanhaiPowerConfig;
    }

    /**
     * 获取缓存服务
     * @return
     */
    public static PowerStoreService loadCacheService(){
        if(powerStoreService==null){
            Logger.info("[ShanhaiPower-InitCache]");
            powerStoreService=SpringBeanUtils.getBean(PowerStoreService.class);
        }
        return powerStoreService;
    }
    /**
     * 获取权限服务
     * @return
     */
    public static PermissionService loadPermissionService(){
        if(permissionService==null){
            Logger.info("[ShanhaiPower-InitPermission]");
            permissionService=SpringBeanUtils.getBean(PermissionService.class);
        }
        return permissionService;
    }
    /**
     * 生成Token
     * @param shanhaiPowerConfig
     * @return
     */
    public static String generateToken(ShanhaiPowerConfig shanhaiPowerConfig, Map<String, Object> extParams){
        if (tokenGenerateService == null) {
            tokenGenerateService=SpringBeanUtils.getBean(TokenGenerateService.class);
        }
        return tokenGenerateService.generateToken(shanhaiPowerConfig,extParams);
    }
    /**
     * 登录锁定判断
     * @param userFlag 用户标识
     *
     * @return boolean
     */
    public static boolean loginLock(Object userFlag){
          return loginLock(userFlag,"Default");
    }
    /**
     * 登录锁定判断
     * @param userFlag 用户标识
     * @param channel  渠道标识
     *
     * @return boolean
     */
    public static boolean loginLock(Object userFlag,String channel){
        String lgErrorNum="shanhaipower:login:errornum:"+userFlag+":"+channel;
        PowerStoreService powerStoreService= loadCacheService();
        if(powerStoreService.exists(lgErrorNum)){
            return true;
        }
        return false;
    }
    /**
     * 登录失败
     * @param userFlag 用户标识
     */
    public static void loginFailure(Object userFlag){
        loginFailure(userFlag,"Default");
    }
    /**
     * 登录失败
     * @param userFlag 用户标识
     * @param channel 渠道标识
     */
    public static void loginFailure(Object userFlag,String channel){
        String lgErrorNum="shanhaipower:login:errornum:"+userFlag+":"+channel;
        String lockKey="shanhaipower:login:lock:"+userFlag+":"+channel;
        PowerStoreService powerStoreService= loadCacheService();
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        if(powerStoreService.exists(lgErrorNum)){
            int num= Integer.parseInt(String.valueOf(powerStoreService.get(lgErrorNum)));
            num++;
            powerStoreService.set(lgErrorNum,String.valueOf(num),shanhaiPowerConfig.getLockThresholdExpire());
            if(num>shanhaiPowerConfig.getLockThreshold()){
                powerStoreService.set(lockKey,String.valueOf(num),shanhaiPowerConfig.getLockExpire());
            }
        }else{
            powerStoreService.set(lgErrorNum,String.valueOf(1),shanhaiPowerConfig.getLockThresholdExpire());
        }
    }
}
