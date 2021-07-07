package com.xinxin.eshop.inventory.listener;

import com.xinxin.eshop.inventory.thread.RequestProcessorThreadPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @ClassName InitListener
 * @Description 初始化线程池对象
 * @Author lantianbaiyun
 * @Date 2021-07-07
 * @Version 1.0
 */
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 初始化工作线程池和内存队列
        RequestProcessorThreadPool.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
