package com.wangshanhai.power.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
     * token状态 -3会话超时 -1 会话失效  -2:互斥登录被踢  1:正常
     */
    private Integer status;
    /**
     * Token生成渠道
     */
    private String loginChannel;
    /**
     * Token扩展参数
     */
    private Map<String,Object> extParams;
}
