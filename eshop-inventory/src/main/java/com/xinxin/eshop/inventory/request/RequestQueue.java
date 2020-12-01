package com.xinxin.eshop.inventory.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存内存队列
 * 单例模式
 */
public class RequestQueue {

    /**
     * 内存队列集合
     */
    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    /**
     * 标识位map
     * key：请求商品ID value：请求类型标识
     * true：更新请求
     * false：读请求
     * null：从未进入队列
     */
    private Map<Integer,Boolean> flagMap = new ConcurrentHashMap<Integer,Boolean>();

    private static class Singleton {
        private static RequestQueue instance;

        static {
            instance = new RequestQueue();
        }

        public static RequestQueue getInstance() {
            return instance;
        }
    }

    /**
     * 获取RequestQueue实例
     *
     * @return
     */
    public static RequestQueue getInstance() {
        return Singleton.getInstance();
    }

    /**
     * 添加内存队列
     * @param queue
     */
    public void addQueue(ArrayBlockingQueue<Request> queue) {
        this.queues.add(queue);
    }

    /**
     * 获取队列集合中队列的个数
     * @return
     */
    public int queueSize(){
        return queues.size();
    }

    /**
     * 通过下表获取集合中的内存队列
     * @param index
     * @return
     */
    public ArrayBlockingQueue<Request> getQueue(int index){
        return queues.get(index);
    }

    /**
     * 获取标识位
     * @return
     */
    public Map<Integer,Boolean> getFlagMap(){
        return flagMap;
    }
}
