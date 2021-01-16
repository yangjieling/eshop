package com.xinxin.eshop.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.model.ShopInfo;
import com.xinxin.eshop.cache.rebuild.RebuildCacheQueue;
import com.xinxin.eshop.cache.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {
    private static final Logger log = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheService cacheService;

    @RequestMapping("/testPutCache")
    public String testPutCache(ProductInfo productInfo) {
        log.info(productInfo.toString());
        cacheService.saveLocalCache(productInfo);
        return "success";
    }

    @RequestMapping("/testGetCache")
    public ProductInfo testGetCache(Long productId) {
        log.info(productId.toString());
        return cacheService.getLocalCache(productId);
    }

    /**
     * 获取商品信息
     *
     * @param productId
     * @return
     */
    @GetMapping("/getProductInfo")
    public ProductInfo getProductInfo(Long productId) {
        log.info("getProductInfo请求参数productId：{}", productId);
        ProductInfo productInfo = null;
        // 查询redis cluster商品信息
        productInfo = cacheService.getProductInfoFromRedisCache(productId);
        if (productInfo == null) {
            log.info("redis cluster中的缓存信息为空");
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
        }

        if (productInfo == null) {
            log.info("local cache [ehCache] 中的缓存信息为空");
            // 从数据源拉取数据
            // 模拟从商品服务获取到了最新的数据

            String productInfoJSON = "{\"id\":" + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:01:00\"}";
            // 返回给nginx
            productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

            // 异步发送一条变更消息到内存队列中 让后台线程进行缓存重建
            RebuildCacheQueue.getInstance().putProductInfo(productInfo);
        }
        log.info("getProductInfo请求处理成功，响应结果：{}", JSONObject.toJSONString(productInfo));
        return productInfo;
    }

    /**
     * 获取店铺信息
     *
     * @param shopId
     * @return
     */
    @GetMapping("/getShopInfo")
    public ShopInfo getShopInfo(Long shopId) {
        log.info("getProductInfo请求参数shopId：{}", shopId);
        ShopInfo shopInfo = null;
        // 查询redis cluster商品信息
        shopInfo = cacheService.getShopInfoFromRedisCache(shopId);
        if (shopInfo == null) {
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
        }

        if (shopInfo == null) {
            // 从数据源拉取数据

        }
        log.info("getProductInfo请求处理成功，响应结果：{}", JSONObject.toJSONString(shopInfo));
        return shopInfo;
    }
}
