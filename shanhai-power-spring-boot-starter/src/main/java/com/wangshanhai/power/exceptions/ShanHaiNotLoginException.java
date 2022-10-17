package com.wangshanhai.power.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 未登录异常
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShanHaiNotLoginException extends  ShanHaiPowerException{
    private String code="10001";
    private String message;
    public ShanHaiNotLoginException(String message) {
        this.message = message;
    }
}
