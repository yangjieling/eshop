package com.xinxin.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.*;

public class ExampleThreadPoolIsolationStrategyCommand extends HystrixCommand<String> {
    private static final Setter cachedSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleInfoService"))// command group
            .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld"))// command key(command name)
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("HelloWorldPool"))// 单独指定使用另一个线程池的线程
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(10)// 设置线程池的大小 默认是10
                    .withQueueSizeRejectionThreshold(5))// 线程池的队列大小阈值 默认值为5 超过该阈值 将执行拒绝策略
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
            .withFallbackIsolationSemaphoreMaxConcurrentRequests(10));// 设置调用command的fallback方法的线程资源数量 默认是10 是基于信号量（semaphore）实现的

    protected ExampleThreadPoolIsolationStrategyCommand(Setter setter) {
        super(cachedSetter);
    }

    @Override
    protected String run() throws Exception {
        return null;
    }
}
