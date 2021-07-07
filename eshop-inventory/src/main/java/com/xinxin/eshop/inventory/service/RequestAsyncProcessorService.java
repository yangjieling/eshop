package com.xinxin.eshop.inventory.service;

import com.xinxin.eshop.inventory.request.Request;

/**
 * @ClassName RequestAsyncProcessorService
 * @Description TODO
 * @Author lantianbaiyun
 * @Date 2021-07-07
 * @Version 1.0
 */
public interface RequestAsyncProcessorService {

    /**
     * 通过商品ID将对商铺的处理路由到某一个队列中
     *
     * @param request
     */
    void process(Request request);
}
