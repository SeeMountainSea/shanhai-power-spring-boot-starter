package com.wangshanhai.power.annotation;

import com.wangshanhai.power.PowerImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用山海Power
 * @author Shmily
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(PowerImportSelector.class)
public @interface EnableShanHaiPower {
}
