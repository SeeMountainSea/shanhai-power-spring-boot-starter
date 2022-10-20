package com.wangshanhai.power.config;

import com.wangshanhai.power.dto.RoutePermission;
import com.wangshanhai.power.exceptions.ShanHaiNotPermissionException;
import com.wangshanhai.power.open.ShanhaiPower;
import com.wangshanhai.power.service.PermissionService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * 权限拦截器
 * @author Shmily
 */
public class ShanhaiPowerRoutePermissionsInterceptor  extends HandlerInterceptorAdapter {
    /**
     * 用户权限有效性校验
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        PermissionService permissionService= ShanhaiPower.loadPermissionService();
        ShanhaiPowerConfig shanhaiPowerConfig= ShanhaiPower.getConfig();
        //自定义路由规则优先级最高
        List<RoutePermission> routePermissions=permissionService.loadRoutePermissionConfig();
        if(CollectionUtils.isEmpty(routePermissions)){
            routePermissions=shanhaiPowerConfig.getRoutePermissions();
        }
        if(!CollectionUtils.isEmpty(routePermissions)){
            Optional<RoutePermission> t= routePermissions.stream().filter(routePermission -> request.getRequestURI().startsWith(routePermission.getPath().replace("/**",""))).findFirst();
            if(t.isPresent()){
                List<String> allPermission= permissionService.queryAllPermission(request);
                if(!CollectionUtils.isEmpty(allPermission)&& !allPermission.contains(t.get().getPermission())){
                    throw new ShanHaiNotPermissionException("您没有操作该资源的权限");
                }
            }
        }
        return true;
    }



}
