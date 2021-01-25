package com.xinxin.eshop.cache.ha.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixObservableCommand;
import com.xinxin.eshop.cache.ha.http.HttpClientUtils;
import com.xinxin.eshop.cache.ha.hystrix.command.GetCityNameCommand;
import com.xinxin.eshop.cache.ha.hystrix.command.GetProductInfoCommand;
import com.xinxin.eshop.cache.ha.hystrix.command.GetProductInfosCommand;
import com.xinxin.eshop.cache.ha.model.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Observer;

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

    @GetMapping("/getProductInfo")
    public String getProductInfo(Long productId) {
        HystrixCommand<ProductInfo> getProductInfoCommand = new GetProductInfoCommand(productId);
        ProductInfo productInfo = getProductInfoCommand.execute();

        GetCityNameCommand getCityNameCommand = new GetCityNameCommand(productInfo.getCityId());
        String cityName = getCityNameCommand.execute();
        productInfo.setCityName(cityName);

        log.info(productInfo.toString());
        return "success";
    }

    /**
     * 一次性批量查询多条商品数据的请求
     *
     * @param productIds
     * @return
     */
    @GetMapping("/getProductInfos")
    public String getProductInfos(String productIds) {
        HystrixObservableCommand<ProductInfo> getProductInfosCommand = new GetProductInfosCommand(productIds.split(","));
        Observable<ProductInfo> observe = getProductInfosCommand.observe();
        observe.subscribe(new Observer<ProductInfo>() {

            @Override
            public void onCompleted() {
                log.info("获取完了所有的商品数据");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ProductInfo productInfo) {
                log.info(productInfo.toString());
            }
        });

        return "success";
    }
}
