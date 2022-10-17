package com.wangshanhai.power.annotation;

import java.lang.annotation.*;

/**
 * 免鉴权
 * @author Shmily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestNotNeedAuth  {
}
