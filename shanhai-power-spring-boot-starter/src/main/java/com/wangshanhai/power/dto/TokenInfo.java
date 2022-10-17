package com.wangshanhai.power.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 令牌信息
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户IP
     */
    private String createIP;
    /**
     * token值
     */
    private String token;
    /**
     * 生成时间
     */
    private Date createTime;
    /**
     * 最后访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastAccessTime;
    /**
     * 会话超时时间 单位:秒
     */
    private Integer maxActiveTime;
    /**
     * 当前用户标识
     */
    private Object userFlag;
    /**
     * token状态 -1 会话过期  -2:互斥登录被踢 -3:注销会话 1:正常
     */
    private Integer status;
    /**
     * Token生成渠道
     */
    private String loginChannel;
}
