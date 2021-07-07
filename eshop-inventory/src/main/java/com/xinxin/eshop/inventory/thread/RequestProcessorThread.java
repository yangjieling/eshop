package com.xinxin.eshop.inventory.thread;

import com.xinxin.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.xinxin.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.xinxin.eshop.inventory.request.Request;
import com.xinxin.eshop.inventory.request.RequestQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * @ClassName RequestProcessorThread
 * @Description 工作线程
 * @Author lantianbaiyun
 * @Date 2021-07-07
 * @Version 1.0
 */
public class RequestProcessorThread implements Callable<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(RequestProcessorThread.class);

    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue) {
        this.queue = queue;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            while (true) {
                log.info("后台工作线程开始处理请求，目前队列中排队的请求个数为：{},排队的元素：{}", queue.size(), queue.toString());
                Request request = queue.take();
                RequestQueue requestQueue = RequestQueue.getInstance();
                Map<Integer, Boolean> flagMap = requestQueue.getFlagMap();
                if (!request.isForceReFresh()) {
                    // 读请求过滤
                    if (request instanceof ProductInventoryDBUpdateRequest) {
                        // 是更新请求 将标志为设置为true
                        flagMap.put(request.getProductId(), true);
                    } else if (request instanceof ProductInventoryCacheRefreshRequest) {
                        Boolean flag = flagMap.get(request.getProductId());
                        if (flag == null) {
                            // 队列从未处理读请求和更新请求
                            flagMap.put(request.getProductId(), false);
                        }

                        if (flag != null && flag) {
                            // 有更新请求 没有读请求
                            flagMap.put(request.getProductId(), false);
                        }

                        if (flag != null && !flag) {
                            // 有读请求在前面排队
                            continue;
                        }
                    }
                }

                log.info("后台工作线程开始处理请求商品ID:{}", request.getProductId());
                request.process();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}