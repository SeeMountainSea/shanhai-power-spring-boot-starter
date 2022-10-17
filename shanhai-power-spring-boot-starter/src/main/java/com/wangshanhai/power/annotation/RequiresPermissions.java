package com.wangshanhai.power.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * @author Shmily
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    String [] value();
}
