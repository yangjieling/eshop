package com.xinxin.eshop.cache.service;

import com.xinxin.eshop.cache.model.ProductInfo;

public interface CacheService {

    ProductInfo saveLocalCache(ProductInfo productInfo);

    ProductInfo getLocalCache(Long productId);
}
