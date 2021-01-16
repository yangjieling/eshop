package com.xinxin.eshop.cache.service;

import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.model.ShopInfo;

public interface CacheService {

    ProductInfo saveLocalCache(ProductInfo productInfo);

    ProductInfo getLocalCache(Long productId);

    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    ProductInfo getProductInfoFromLocalCache(Long productId);

    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    ShopInfo getShopInfoFromLocalCache(Long shopId);

    void saveProductInfo2RedisCache(ProductInfo productInfo);

    void saveShopInfo2RedisCache(ShopInfo shopInfo);

    ProductInfo getProductInfoFromRedisCache(Long productId);

    ShopInfo getShopInfoFromRedisCache(Long shopId);
}
