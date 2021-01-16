package com.xinxin.eshop.cache.rebuild;

import com.xinxin.eshop.cache.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存的内存消息队列
 */
public class RebuildCacheQueue {
    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<>(1000);

    /**
     * 消息对象入队操作
     * @param productInfo
     */
    public void putProductInfo(ProductInfo productInfo){
        try {
            queue.put(productInfo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从内存消息队列中取出消息对象
     * @return
     */
    public ProductInfo tackProductInfo(){
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RebuildCacheQueue() {
    }

    private static class Singleton {
        private static RebuildCacheQueue instance;

        static {
            instance = new RebuildCacheQueue();
        }

        public static RebuildCacheQueue getInstance() {
            return instance;
        }
    }

    public static RebuildCacheQueue getInstance() {
        return Singleton.getInstance();
    }

    public static void init() {
        getInstance();
    }
}
