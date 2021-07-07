package com.xinxin.eshop.inventory.request;

/**
 * @ClassName Request
 * @Description 请求对象接口
 * @Author lantianbaiyun
 * @Date 2021-07-07
 * @Version 1.0
 */
public interface Request {

    /**
     * 处理方法
     */
    void process();

    /**
     * 获取商品ID
     *
     * @return
     */
    Integer getProductId();

    /**
     * 获取是否强制刷新
     *
     * @return
     */
    boolean isForceReFresh();
}
