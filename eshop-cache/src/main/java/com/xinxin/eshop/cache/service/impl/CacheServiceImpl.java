package com.xinxin.eshop.cache.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.model.ShopInfo;
import com.xinxin.eshop.cache.service.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * 将数据保存到本地ehcache缓存中
     *
     * @param productInfo
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "'key_' + #productInfo.getId()")
    public ProductInfo saveLocalCache(ProductInfo productInfo) {
        System.out.println(productInfo.toString());
        return productInfo;
    }

    /**
     * 从本地ehcache中查询缓存
     *
     * @param productId
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'key_' + #productId")
    public ProductInfo getLocalCache(Long productId) {
        System.out.println(productId.toString());
        return null;
    }

    /**
     * 将商品信息保存到本地缓存中
     *
     * @param productInfo
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "'productInfo_'+#productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    /**
     * 从本地缓存中获取商品信息
     *
     * @param productId
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'productInfo_'+#productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    /**
     * 将店铺信息保存到本地缓存中
     *
     * @param shopInfo
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "'shopInfo_'+#shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    /**
     * 从本地缓存中查询店铺信息
     *
     * @param shopId
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "'shopInfo_'+#shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }

    /**
     * 将商品信息保存到redis cluster缓存中
     *
     * @param productInfo
     */
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        jedisCluster.set("productInfo_" + productInfo.getId(), JSONObject.toJSONString(productInfo));
    }

    /**
     * 从redis cluster中查询缓存的商品信息
     *
     * @param productId
     * @return
     */
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        ProductInfo productInfo = null;
        String key = "productInfo_" + productId;
        String resultJson = jedisCluster.get(key);
        if (StringUtils.isNotBlank(resultJson)) {
            productInfo = JSONObject.parseObject(resultJson, ProductInfo.class);
        }
        return productInfo;
    }

    /**
     * 将店铺信息保存到redis cluster缓存中
     *
     * @param shopInfo
     */
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        jedisCluster.set("shopInfo_" + shopInfo.getId(), JSONObject.toJSONString(shopInfo));
    }

    /**
     * 从redis cluster中查询店铺信息
     * @param shopId
     * @return
     */
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        ShopInfo shopInfo = null;
        String key = "shopInfo_" + shopId;
        String resultJson = jedisCluster.get(key);
        if (StringUtils.isNotBlank(resultJson)) {
            shopInfo = JSONObject.parseObject(resultJson, ShopInfo.class);
        }
        return shopInfo;
    }
}
