package com.xinxin.eshop.cache.controller;

import com.xinxin.eshop.cache.model.ProductInfo;
import com.xinxin.eshop.cache.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {
    private static Logger log = LoggerFactory.getLogger(CacheController.class);

    @Autowired
    private CacheService cacheService;

    @RequestMapping("/testPutCache")
    public String testPutCache(ProductInfo productInfo){
        log.info(productInfo.toString());
        cacheService.saveLocalCache(productInfo);
        return "success";
    }

    @RequestMapping("/testGetCache")
    public ProductInfo testGetCache(Long productId){
        log.info(productId.toString());
        return cacheService.getLocalCache(productId);
    }
}
