package com.xinxin.eshop.cache.ha.controller;

import com.xinxin.eshop.cache.ha.http.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存服务接口
 */
@RestController
public class CacheController {
    private static final Logger log = LoggerFactory.getLogger(CacheController.class);

    @GetMapping("/change/product")
    public String changeProduct(Long productId) {
        log.info("请求参数:{}", productId);
        String url = "http://192.168.0.151:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
        log.info(response);
        return "success";
    }
}
