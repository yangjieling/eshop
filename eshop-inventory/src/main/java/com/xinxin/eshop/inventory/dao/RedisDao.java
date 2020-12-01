package com.xinxin.eshop.inventory.dao;

public interface RedisDao {
    /**
     * 设置redis缓存信息
     * @param key
     * @param value
     */
    void set(String key,String value);

    /**
     * 获取redis缓存信息
     * @param key
     * @return
     */
    String getValue(String key);

    /**
     * 删除库存
     * @param key
     */
    void delete(String key);
}
