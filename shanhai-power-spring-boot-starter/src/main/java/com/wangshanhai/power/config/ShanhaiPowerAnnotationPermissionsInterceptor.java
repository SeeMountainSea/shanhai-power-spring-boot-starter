package com.wangshanhai.power.config;

import com.wangshanhai.power.annotation.RequiresPermissions;
import com.wangshanhai.power.exceptions.ShanHaiNotPermissionException;
import com.wangshanhai.power.open.ShanhaiPower;
import com.wangshanhai.power.service.PermissionService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 权限拦截器
 * @author Shmily
 */
public class ShanhaiPowerAnnotationPermissionsInterceptor implements HandlerInterceptor {
    /**
     * 用户权限有效性校验
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        PermissionService permissionService= ShanhaiPower.loadPermissionService();
        if(!(handler instanceof  HandlerMethod)){
           return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresPermissions requiresPermissions=handlerMethod.getMethodAnnotation(RequiresPermissions.class);
        if(requiresPermissions!=null){
            List<String> allPermission= permissionService.queryAllPermission(request);
            if(!CollectionUtils.isEmpty(allPermission)){
                for(String permissions:requiresPermissions.value()){
                    if(!allPermission.contains(permissions)){
                        throw new ShanHaiNotPermissionException("您没有操作该资源的权限");
                    }
                }
            }else{
                throw new ShanHaiNotPermissionException("20002","您没有操作该资源的权限");
            }
        }
        return true;
    }
}
