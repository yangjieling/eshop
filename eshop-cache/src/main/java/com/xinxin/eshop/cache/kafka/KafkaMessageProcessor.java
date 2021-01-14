package com.xinxin.eshop.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.model.ShopInfo;
import com.xinxin.eshop.cache.service.CacheService;
import com.xinxin.eshop.cache.spring.SpringContext;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * kafka消息处理线程
 */
public class KafkaMessageProcessor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(KafkaMessageProcessor.class);

    private KafkaStream kafkaStream;
    private CacheService cacheService;

    public KafkaMessageProcessor(KafkaStream kafkaStream) {
        this.kafkaStream = kafkaStream;
        this.cacheService = SpringContext.getApplicationContext().getBean(CacheService.class);
        log.info("初始化kafkaMessageProcessor");
    }

    @Override
    public void run() {
        ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
        while (iterator.hasNext()) {
            try {
                // kafka topic中的消息
                String message = new String(iterator.next().message(), "UTF-8");
                // 将消息转换成JSON对象
                JSONObject messageJSONObject = JSONObject.parseObject(message);
                // 获取服务标识
                String serviceId = messageJSONObject.getString("serviceId");
                if ("productInfoService".equals(serviceId)) {
                    // 商品服务消息标识  调用商品的处理方法
                    processProductInfoChangeMessage(messageJSONObject);
                } else if ("shopInfoService".equals(serviceId)) {
                    // 店铺服务消息标识  调用店铺服务消息处理方法
                    processShopInfoChangeMessage(messageJSONObject);
                }
            } catch (RuntimeException e) {
                log.error("处理消息异常.", e);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理商品信息的改变消息
     *
     * @param messageJSONObject
     */
    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取商品ID
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品信息服务 暴露的接口 获取最新的商品信息
        // 模拟通过调用商品信息服务  获取到最新商品信息数据
        String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1}";
        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

        // 将最新的商品信息保存到本地ehcache缓存中
        cacheService.saveProductInfo2LocalCache(productInfo);
        log.info("保存到本地缓存中的商品信息:{}", cacheService.getProductInfoFromLocalCache(productId));
        // 将最新的商品信息保存到redis cluster缓存中
        cacheService.saveProductInfo2RedisCache(productInfo);
    }

    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        Long productId = messageJSONObject.getLong("productId");
        Long shopId = messageJSONObject.getLong("shopId");

        String shopInfoJSON = "{\"id\": " + shopId + ", \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";
        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);

        cacheService.saveShopInfo2LocalCache(shopInfo);
        log.info("保存到本地缓存中的店铺信息:{}", cacheService.getShopInfoFromLocalCache(shopId));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }


}
