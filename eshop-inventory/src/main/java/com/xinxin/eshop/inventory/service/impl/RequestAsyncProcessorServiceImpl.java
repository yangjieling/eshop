package com.xinxin.eshop.inventory.service.impl;

import com.xinxin.eshop.inventory.request.Request;
import com.xinxin.eshop.inventory.request.RequestQueue;
import com.xinxin.eshop.inventory.service.RequestAsyncProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @ClassName RequestAsyncProcessorServiceImpl
 * @Description 请求异步处理的service
 * 通过商品ID将对商铺的处理路由到某一个队列中
 * @Author lantianbaiyun
 * @Date 2021-07-07
 * @Version 1.0
 */
@Service
public class RequestAsyncProcessorServiceImpl implements RequestAsyncProcessorService {
    private static final Logger log = LoggerFactory.getLogger(RequestAsyncProcessorServiceImpl.class);

    @Override
    public void process(Request request) {
        log.info("请求异步处理service开始进行请求对象路由内存队列");
        try {
            // 做请求的路由 根据商品ID 路由到对应的内存队列中
            ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
            // 将封装的请求放入到队列中 完成路由操作
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取路由到的内存队列
     *
     * @param productId
     * @return
     */
    private ArrayBlockingQueue<Request> getRoutingQueue(Integer productId) {
        RequestQueue requestQueue = RequestQueue.getInstance();
        // 参看hashMap 定位下标的实现算法
        String key = String.valueOf(productId);
        int h;
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        int index = (requestQueue.queueSize() - 1) & hash;
        return requestQueue.getQueue(index);
    }
}
