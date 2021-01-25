package com.xinxin.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.*;

public class ExampleSemaphoreIsolationStrategyCommand extends HystrixCommand<String> {
    private static final Setter cachedSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleInfoService"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("helloWorld"))
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("helloWorldPool"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)// 设置资源隔离粗略为semaphore
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)// 设置使用SEMAPHORE隔离策略的时候，允许访问的最大并发量，超过这个最大并发量，请求直接被reject 默认是10
            .withFallbackIsolationSemaphoreMaxConcurrentRequests(10));// 设置调用command的fallback方法的线程资源数量 默认是10 是基于信号量（semaphore）实现的


    public ExampleSemaphoreIsolationStrategyCommand() {
        super(cachedSetter);
    }

    @Override
    protected String run() throws Exception {
        return null;
    }
}
