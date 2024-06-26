package com.wangshanhai.power.open;

import com.wangshanhai.power.config.ShanhaiPowerConfig;
import com.wangshanhai.power.dto.TokenInfo;
import com.wangshanhai.power.exceptions.ShanHaiBizException;
import com.wangshanhai.power.exceptions.ShanHaiNotLoginException;
import com.wangshanhai.power.service.PermissionService;
import com.wangshanhai.power.service.PowerExtService;
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
    private static PowerExtService powerExtService;

    /**
     * 登录
     * @param userFlag 用户标识
     * @return
     */
    public static TokenInfo login(Object userFlag){
        return login(userFlag,"Default",new HashMap<>(),new HashMap<>());
    }
    /**
     * 登录
     * @param userFlag 用户标识
     * @param tokenExtParams 自定义返回参数
     * @return
     */
    public static TokenInfo login(Object userFlag,Map<String, Object> tokenExtParams){
        return login(userFlag,"Default",new HashMap<>(),tokenExtParams);
    }
    /**
     * 登录
     * @param userFlag 用户标识
     * @param channel 登陆渠道
     * @return
     */
    public static TokenInfo login(Object userFlag,String channel){
        return login(userFlag,channel,new HashMap<>(),new HashMap<>());
    }
    /**
     * 登录
     * @param userFlag 用户标识
     * @param channel 登陆渠道
     * @param tokenExtParams 自定义返回参数
     * @return
     */
    public static TokenInfo login(Object userFlag,String channel,Map<String, Object> tokenExtParams){
        return login(userFlag,channel,new HashMap<>(),tokenExtParams);
    }
    /**
     * 登录
     * @param userFlag 用户标识
     * @param channel  渠道标识
     * @param generateTokenExtParams 生成token自定义策略参数
     * @param tokenExtParams token自定义返回参数
     * @return
     */
    public static TokenInfo login(Object userFlag,String channel, Map<String, Object> generateTokenExtParams,Map<String, Object> tokenExtParams){
        if(StringUtils.isEmpty(channel)){
            channel="Default";
        }
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        if (powerExtService == null) {
            powerExtService=SpringBeanUtils.getBean(PowerExtService.class);
        }
        TokenInfo tokenInfoDTO= TokenInfo.builder()
                .createIP(powerExtService.getIp(HttpContextUtils.getHttpServletRequest()))
                .createTime(new Date())
                .lastAccessTime(new Date())
                .extParams(tokenExtParams)
                .status(1)
                .maxActiveTime(shanhaiPowerConfig.getMaxActiveTime())
                .userFlag(userFlag)
                .token(generateToken(shanhaiPowerConfig,generateTokenExtParams))
                .loginChannel(channel)
                .build();
        String tokenInfoFlag="shanhaipower:"+ userFlag +":"+channel;
        String tokenLoginListFlag="shanhaipower:"+ userFlag +":"+channel+":LoginList";
        String tokenFlag="shanhaipower:"+tokenInfoDTO.getToken();
        if(powerStoreService.lock(tokenInfoFlag)){
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
            String lgErrorNum="shanhaipower:login:errornum:"+userFlag+":"+channel;
            String lockKey="shanhaipower:login:lock:"+userFlag+":"+channel;
            powerStoreService.del(lgErrorNum);
            powerStoreService.del(lockKey);
            powerStoreService.unlock(tokenInfoFlag);
            return tokenInfoDTO;
        }
        throw new ShanHaiBizException("用户登录失败，原因:暂无可用资源！");
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
        String tokenInfoFlag="shanhaipower:"+ userFlag +":"+channel;
        String tokenLoginListFlag="shanhaipower:"+ userFlag +":"+channel+":LoginList";
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
        String token= Objects.requireNonNull(HttpContextUtils.getHttpServletRequest()).getHeader(shanhaiPowerConfig.getTokenName());
        if(StringUtils.isEmpty(token)){
            throw new ShanHaiBizException("获取数据失败，原因:Token不存在");
        }
        TokenInfo tokenInfo=getTokenInfo(token);
        if(tokenInfo==null){
             throw new ShanHaiNotLoginException("获取数据失败，原因:Token失效");
        }
        return tokenInfo;
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
        if(StringUtils.isEmpty(token)){
            throw new ShanHaiBizException("存储数据失败，原因:Token不存在");
        }
        if(getTokenInfo(token)==null){
            throw new ShanHaiNotLoginException("存储数据失败，原因:Token失效");
        }
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token+":session:"+key;
        if(powerStoreService.lock(tokenFlag)){
            if(!powerStoreService.exists(tokenFlag)){
                powerStoreService.set(tokenFlag,data,shanhaiPowerConfig.getTokenSessionTimeout());
            }else{
                powerStoreService.set(tokenFlag,data,powerStoreService.ttl(tokenFlag));
            }
            powerStoreService.unlock(tokenFlag);
        }else{
            throw new ShanHaiBizException("数据设置失败，原因：暂无可用资源！");
        }
    }

    /**
     * 设置Token级会话数据
     * @return
     */
    public static void setTokenSessionData(String token,String key,Object data){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        if(StringUtils.isEmpty(token)){
            throw new ShanHaiBizException("存储数据失败，原因:Token不存在");
        }
        if(getTokenInfo(token)==null){
            throw new ShanHaiNotLoginException("存储数据失败，原因:Token失效");
        }
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token+":session:"+key;
        if(powerStoreService.lock(tokenFlag)){
            if(!powerStoreService.exists(tokenFlag)){
                powerStoreService.set(tokenFlag,data,shanhaiPowerConfig.getTokenSessionTimeout());
            }else{
                powerStoreService.set(tokenFlag,data,powerStoreService.ttl(tokenFlag));
            }
            powerStoreService.unlock(tokenFlag);
        }else{
            throw new ShanHaiBizException("数据设置失败，原因：暂无可用资源！");
        }
    }
    /**
     * 获取Token级会话数据
     * @return
     */
    public static Object getTokenSessionData(String key){
        String token= HttpContextUtils.getHttpServletRequest().getHeader(shanhaiPowerConfig.getTokenName());
        return getTokenSessionData(token,key);
    }
    /**
     * 获取Token级会话数据
     * @return
     */
    public static Object getTokenSessionData(String token,String key){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
        if(StringUtils.isEmpty(token)){
            throw new ShanHaiBizException("获取数据失败，原因:Token不存在");
        }
        if(getTokenInfo(token)==null){
            throw new ShanHaiNotLoginException("获取数据失败，原因:Token失效");
        }
        token=token.replace(tokenPrefix,"");
        String tokenFlag="shanhaipower:"+token+":session:"+key;
        return powerStoreService.get(tokenFlag);
    }
    /**
     * 校验token有效性
     * @return
     */
    public static Integer checkToken(HttpServletRequest request){
        String token= request.getHeader(shanhaiPowerConfig.getTokenName());
        return checkToken(token);
    }
    /**
     * 校验token有效性
     * @return
     */
    public static Integer checkToken(String token){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
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
                    Logger.error("[ShanhaiPower-Token-TimeOut]-tokenInfo:{}", tokenInfo);
                    //会话超时
                    return -3;
                }
            }
        }
        Logger.error("[ShanhaiPower-Token-NotFind]-token:{}", token);
        //会话不存在
        return -1;
    }
    /**
     * 刷新Token有效期
     */
    public static void refreshTokenAccessTime(HttpServletRequest request){
        String token= request.getHeader(shanhaiPowerConfig.getTokenName());
        refreshTokenAccessTime(token);
    }

    /**
     * 刷新Token有效期
     */
    public static void refreshTokenAccessTime(String token){
        ShanhaiPowerConfig  shanhaiPowerConfig=getConfig();
        PowerStoreService powerStoreService= loadCacheService();
        String tokenPrefix= shanhaiPowerConfig.getTokenPrefix();
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
