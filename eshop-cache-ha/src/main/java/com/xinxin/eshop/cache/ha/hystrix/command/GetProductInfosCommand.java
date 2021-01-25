package com.xinxin.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.xinxin.eshop.cache.ha.http.HttpClientUtils;
import com.xinxin.eshop.cache.ha.model.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetProductInfosCommand extends HystrixObservableCommand<ProductInfo> {
    private static final Logger log = LoggerFactory.getLogger(GetProductInfosCommand.class);
    private String[] productIds;

    public GetProductInfosCommand(String[] productIds) {
        super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        this.productIds = productIds;
    }

    @Override
    protected Observable<ProductInfo> construct() {
        Observable<ProductInfo> productInfoObservable = Observable.unsafeCreate(new Observable.OnSubscribe<ProductInfo>() {
            @Override
            public void call(Subscriber<? super ProductInfo> subscriber) {
                try {
                    for (String productId : productIds) {
                        log.info("请求参数:{}", productId);
                        String url = "http://192.168.0.151:8082/getProductInfo?productId=" + productId;
                        String response = HttpClientUtils.sendGetRequest(url);
                        ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
                        log.info(productInfo.toString());
                        subscriber.onNext(productInfo);
                    }
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
        return productInfoObservable;
    }
}
