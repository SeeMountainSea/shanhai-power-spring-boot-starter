package com.wangshanhai.power.examples.service;

import com.wangshanhai.power.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限获取
 * @author Shmily
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Override
    public List<String> queryAllPermission(HttpServletRequest request) {
        List<String> allPermission=new ArrayList<>();
        allPermission.add("user:details");
        allPermission.add("user:route");
        return allPermission;
    }

}
