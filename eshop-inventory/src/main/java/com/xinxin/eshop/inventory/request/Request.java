package com.xinxin.eshop.inventory.request;

/**
 * 请求对象接口
 */
public interface Request {

    /**
     * 处理方法
     */
    void process();

    /**
     * 获取商品ID
     * @return
     */
    Integer getProductId();

    /**
     * 获取是否强制刷新
     * @return
     */
    public boolean isForceReFresh();
}
