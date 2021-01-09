package com.xinxin.eshop.cache.service.impl;

import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.service.CacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";

    /**
     * 将数据保存到本地ehcache缓存中
     * @param productInfo
     * @return
     */
    @CachePut(value = CACHE_NAME,key = "'key_' + #productInfo.getId()")
    public ProductInfo saveLocalCache(ProductInfo productInfo) {
        System.out.println(productInfo.toString());
        return productInfo;
    }

    /**
     * 从本地ehcache中查询缓存
     * @param productId
     * @return
     */
    @Cacheable(value = CACHE_NAME,key = "'key_' + #productId")
    public ProductInfo getLocalCache(Long productId){
        System.out.println(productId.toString());
        return null;
    }
}
