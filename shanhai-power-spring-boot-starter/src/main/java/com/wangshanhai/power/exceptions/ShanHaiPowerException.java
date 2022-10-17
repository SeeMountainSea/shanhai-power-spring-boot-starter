package com.wangshanhai.power.exceptions;

import lombok.Data;

/**
 * 自定义异常
 * @author Shmily
 */
@Data
public class ShanHaiPowerException extends RuntimeException {
    private String code;
}