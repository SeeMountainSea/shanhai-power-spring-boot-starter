package com.wangshanhai.power.service;

/**
 * 会话存储服务
 * @author Shmily
 */
public interface PowerStoreService {

    /**
     * 设置缓存失效时间
     * @param key
     * @param time (单位s)
     * @return
     */
    Long expire(String key, int time);

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    boolean exists(String key);
    /**
     * 查询key过期时间
     * @param key
     * @return
     */
    long ttl(String key);
    /**
     * 删除key
     * @param key
     * @return
     */
    void del(String key);
    /**
     * 读取key对应的值
     * @param key
     * @return
     */
    Object get(String key);
    /**
     * 设置key:value
     * @param key
     * @return
     */
    void set(String key, Object value);
    /**
     * 设置key和过期时间
     * @param key
     * @return
     */
    void set(String key, Object value, long time);

}
