package com.wangshanhai.power.service;

import com.wangshanhai.power.dto.RoutePermission;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限服务
 * @author Shmily
 */
public interface PermissionService {
    /**
     * 查询当前用户全部权限
     *
     * @param request
     * @return
     */
    public List<String> queryAllPermission(HttpServletRequest request);

    /**
     * 获取路由权限
     * @return
     */
    default List<RoutePermission> loadRoutePermissionConfig() {
        return null;
    }
}
