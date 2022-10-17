package com.wangshanhai.power;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 自定义注解扫描的自动配置组件
 * @author Shmily
 */
public class PowerImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                "com.wangshanhai.power.config.ShanHaiPowerConfigurer",
                "com.wangshanhai.power.utils.SpringBeanUtils"
        };
    }
}
