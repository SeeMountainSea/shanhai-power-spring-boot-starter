package com.wangshanhai.power.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 路由权限
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePermission {
    /**
     * 路由信息
     */
    private String path;
    /**
     * 权限信息
     */
    private String permission;
}
