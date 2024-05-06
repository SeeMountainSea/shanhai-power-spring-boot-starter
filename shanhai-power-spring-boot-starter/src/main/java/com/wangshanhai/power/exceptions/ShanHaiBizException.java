package com.wangshanhai.power.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据读取异常
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShanHaiBizException extends  ShanHaiPowerException{
    private String code="30001";
    private String message;
    public ShanHaiBizException(String message) {
        this.message = message;
    }
}
