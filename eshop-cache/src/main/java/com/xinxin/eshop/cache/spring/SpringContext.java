package com.xinxin.eshop.cache.spring;

import org.springframework.context.ApplicationContext;

/**
 * 获取spring上下文
 * 将spring上下文抽取到该类中
 * 方便在程序的不方便通过注解注入对象地方通过代码调用
 */
public class SpringContext {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }
}
