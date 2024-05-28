package com.wangshanhai.power.service.impl;

import com.wangshanhai.power.service.PowerExtService;

import javax.servlet.http.HttpServletRequest;

/**
 * 扩展服务
 * @author Shmily
 */
public class PowerExtServiceImpl implements PowerExtService {
    @Override
    public String getIp(HttpServletRequest request) {
        if(request!=null){
            return request.getRemoteHost();
        }
        return "-";
    }
}
