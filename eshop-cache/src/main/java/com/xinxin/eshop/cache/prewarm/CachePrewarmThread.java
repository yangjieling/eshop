package com.xinxin.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.service.CacheService;
import com.xinxin.eshop.cache.spring.SpringContext;
import com.xinxin.eshop.cache.zk.ZookeeperSession;
import org.apache.commons.lang3.StringUtils;

/**
 * 缓存预热线程
 * 服务启动的时候执行一次
 * 通过storm计算的热点productId到商品服务中获取数据
 * 将数据更新到ehcache缓存中和redis cluster缓存中
 */
public class CachePrewarmThread extends Thread {
    @Override
    public void run() {
        try {
            CacheService cacheService = SpringContext.getApplicationContext().getBean(CacheService.class);
            ZookeeperSession zkSession = ZookeeperSession.getInstance();
            String path = "/taskId-list";
            String taskIdList = zkSession.getNodeData(path);
            if (StringUtils.isNotBlank(taskIdList)) {
                String[] taskIdListSplited = taskIdList.split(",");
                for (String taskId : taskIdListSplited) {
                    String taskIdLockPath = "/taskId-lock-" + taskId;
                    boolean result = zkSession.acquireFastFailedDistributedLock(taskIdLockPath);
                    if (!result) {
                        continue;
                    }

                    String taskIdStatusLockPath = "/taskId-status-lock-" + taskId;
                    zkSession.acquireDistributeLock(taskIdStatusLockPath);
                    String taskIdStatusPath = "/taskId-status-" + taskId;
                    String taskIdStatus = zkSession.getNodeData(taskIdStatusPath);
                    if (StringUtils.isBlank(taskIdStatus)) {
                        String hotProductIdList = zkSession.getNodeData("/task-hot-product-list-" + taskId);
                        JSONArray productIdList = JSONArray.parseArray(hotProductIdList);
                        for (int i = 0; i < productIdList.size(); i++) {
                            Long productId = productIdList.getLong(i);
                            // 模拟从商品服务拉取商品信息
                            String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                            ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                            cacheService.saveProductInfo2LocalCache(productInfo);
                            cacheService.saveProductInfo2RedisCache(productInfo);
                        }

                        zkSession.createNode(taskIdStatusPath);
                        zkSession.setNodeData(taskIdStatusPath, "success");
                    }

                    zkSession.releaseDistributeLock(taskIdStatusLockPath);
                    zkSession.releaseDistributeLock(taskIdLockPath);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
