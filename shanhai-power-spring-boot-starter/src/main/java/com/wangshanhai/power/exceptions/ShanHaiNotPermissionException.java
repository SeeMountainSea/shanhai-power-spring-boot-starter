package com.wangshanhai.power.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 细粒度权限异常
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShanHaiNotPermissionException extends  ShanHaiPowerException{
    private String code="20001";
    private String message;
    public ShanHaiNotPermissionException(String message) {
        this.message = message;
    }
}
