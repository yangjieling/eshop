package com.xinxin.eshop.cache.rebuild;

import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.service.CacheService;
import com.xinxin.eshop.cache.spring.SpringContext;
import com.xinxin.eshop.cache.zk.ZookeeperSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 缓存重建线程
 * 处理缓存重建队列RebuildCacheQueue中的任务
 */
public class RebuildCacheThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RebuildCacheThread.class);
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run() {
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZookeeperSession zookeeperSession = ZookeeperSession.getInstance();
        CacheService cacheService = SpringContext.getApplicationContext().getBean(CacheService.class);
        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.tackProductInfo();
            zookeeperSession.acquireDistributeLock(productInfo.getId());
            ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());
            if (existedProductInfo != null) {
                LocalDateTime dateTime = LocalDateTime.parse(productInfo.getModifiedTime(), dtf);
                LocalDateTime existedDateTime = LocalDateTime.parse(existedProductInfo.getModifiedTime(), dtf);
                if (dateTime.isBefore(existedDateTime)) {
                    log.info("current modifyDate [{}] is before existed modifyDate [{}]", dtf.format(dateTime), dtf.format(existedDateTime));
                    return;
                }
                log.info("current modifyDate [{}] is after existed modifyDate [{}]", dtf.format(dateTime), dtf.format(existedDateTime));
            } else {
                log.info("existedProduct info is null ......");
            }
            // 将最新的商品信息保存到本地ehcache缓存中
            cacheService.saveProductInfo2LocalCache(productInfo);
            // 将最新的商品信息保存到redis cluster缓存中
            cacheService.saveProductInfo2RedisCache(productInfo);
            zookeeperSession.releaseDistributeLock(productInfo.getId());
            log.info("new product info save to redis is success productInfo [{}] and from zookeeper lock is release success",productInfo);
        }
    }
}
