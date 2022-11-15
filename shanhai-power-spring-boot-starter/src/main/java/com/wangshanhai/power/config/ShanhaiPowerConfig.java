package com.wangshanhai.power.config;

import com.wangshanhai.power.dto.RoutePermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * ShanhaiPowerConfig
 * @author Shmily
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "shanhai.power")
public class ShanhaiPowerConfig {
    /**
     * Token名称
     */
    private String tokenName ="token";
    /**
     * Token生成算法
     */
    private String tokenAlgorithm="uuid";
    /**
     * Token前缀
     */
    private String tokenPrefix="";
    /**
     * 会话超时时间 单位:秒
     */
    private Integer maxActiveTime=1800;
    /**
     * token有效期 单位:秒
     */
    private Integer tokenTimeout=60*60*24;
    /**
     * token关联Session剩余时间 单位:秒
     */
    private Integer tokenSessionTimeout=60*60*24;
    /**
     * 同端互斥登录
     */
    private Boolean exclusiveLogin=false;
    /**
     * 是否启用自注册
     */
    private Boolean autoRegist=true;
    /**
     * 启用路由权限（默认关闭）
     */
    private Boolean routePermissionEnable=false;
    /**
     * 启用注解权限（默认启用）
     */
    private Boolean annotationPermissionsEnable=true;
    /**
     * 锁定阈值（默认:5）
     */
    private Integer lockThreshold=5;
    /**
     * 锁定阈值累计有效期（默认:1小时）
     */
    private Integer lockThresholdExpire=3600;
    /**
     * 锁定时长（单位:s,默认:30分钟）
     */
    private Integer lockExpire=1800;
    /**
     * 路由权限
     */
    private List<RoutePermission> routePermissions;
    /**
     * 拦截范围(用户会话鉴权)
     */
    private List<String> authPathPatterns=new ArrayList<>();
    /**
     * 不拦截范围(用户会话鉴权)
     */
    private List<String> authExcludePathPatterns=new ArrayList<>();
    /**
     * 拦截范围(用户操作鉴权)
     */
    private List<String> permissionPathPatterns=new ArrayList<>();
    /**
     * 不拦截范围(用户操作鉴权)
     */
    private List<String> permissionExcludePathPatterns=new ArrayList<>();
}


