package com.xinxin.eshop.inventory.thread;

import com.xinxin.eshop.inventory.request.Request;
import com.xinxin.eshop.inventory.request.RequestQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 初始化线程池
 * 单例模式
 */
public class RequestProcessorThreadPool {

    // 创建线程池 最大线程数量是10个
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private RequestProcessorThreadPool() {
        RequestQueue requestQueue = RequestQueue.getInstance();
        for (int i = 0; i < 10; i++) {
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<>(100);
            requestQueue.addQueue(queue);
            executorService.submit(new RequestProcessorThread(queue));
        }
    }

    /**
     * 静态内部类实现单例模式
     * 通过JVM类加载机制 实现类在加载时保证线程安全
     */
    private static class Singleton {
        private static RequestProcessorThreadPool instance;

        static {
            instance = new RequestProcessorThreadPool();
        }

        public RequestProcessorThreadPool getInstance() {
            return instance;
        }
    }

    /**
     * 获取自定义线程池实例
     *
     * @return
     */
    public static RequestProcessorThreadPool getInstance() {
        return Singleton.instance;
    }

    /**
     * 初始化自定义线程池
     */
    public static void init() {
        getInstance();
    }
}
