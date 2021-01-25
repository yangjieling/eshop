package com.xinxin.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.xinxin.eshop.cache.ha.http.HttpClientUtils;
import com.xinxin.eshop.cache.ha.model.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {
    private static final Logger log = LoggerFactory.getLogger(GetProductInfoCommand.class);
    private Long productId;

    public GetProductInfoCommand(Long productId) {
        super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        log.info("请求参数:{}", productId);
        String url = "http://192.168.0.151:8082/getProductInfo?productId=" + productId;
        String response = HttpClientUtils.sendGetRequest(url);
        ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
        log.info(productInfo.toString());
        return productInfo;
    }
}
