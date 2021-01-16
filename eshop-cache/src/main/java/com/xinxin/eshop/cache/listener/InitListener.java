package com.xinxin.eshop.cache.listener;

import com.xinxin.eshop.cache.kafka.KafkaConsumer;
import com.xinxin.eshop.cache.rebuild.RebuildCacheThread;
import com.xinxin.eshop.cache.spring.SpringContext;
import com.xinxin.eshop.cache.zk.ZookeeperSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 自定义初始化鉴定其
 */
public class InitListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(InitListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 获取servlet上下文
        ServletContext servletContext = sce.getServletContext();
        // 获取spring容器
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        // 将spring容器保存到代码中
        SpringContext.setApplicationContext(applicationContext);

        // 初始化KafkaConsumer线程
        new Thread(new KafkaConsumer("cache-message")).start();
        // 启动缓存重建处理线程
        new Thread(new RebuildCacheThread()).start();
        log.info("自定义初始化监听器创建成功！");
        ZookeeperSession.init();
        log.info("初始化zookeeperSession成功");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
