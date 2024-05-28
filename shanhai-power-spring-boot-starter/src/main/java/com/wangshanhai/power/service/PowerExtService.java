package com.wangshanhai.power.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 扩展服务
 * @author Shmily
 */
public interface PowerExtService {
    /**
     * 获取用户IP
     * @param request
     * @return
     */
    public String getIp(HttpServletRequest request);
}
