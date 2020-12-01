package com.xinxin.eshop.inventory.controller;

import com.xinxin.eshop.inventory.model.ProductInventory;
import com.xinxin.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.xinxin.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.xinxin.eshop.inventory.service.ProductInventoryService;
import com.xinxin.eshop.inventory.service.RequestAsyncProcessorService;
import com.xinxin.eshop.inventory.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductInventoryController {
    private static final Logger log = LoggerFactory.getLogger(ProductInventoryController.class);

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private RequestAsyncProcessorService requestAsyncProcessorService;

    /**
     * 更新数据库商品库存信息
     *
     * @param productInventory
     * @return
     */
    @PostMapping("/updateProductInventory")
    public Response updateProductInventory(ProductInventory productInventory) {
        log.info("更新数据库请求开始，请求商品ID:{},请求商品库存:{}", productInventory.getProductId(),productInventory.getProductInventoryCnt());
        // 封装请求对象
        ProductInventoryDBUpdateRequest request = new ProductInventoryDBUpdateRequest(productInventory, productInventoryService);
        // 将请求通过异步处理service路由到内存队列中 由后续相应的线程处理
        requestAsyncProcessorService.process(request);
        return null;
    }

    @GetMapping("/getProductInventory")
    public ProductInventory getProductInventory(Integer productId) {
        ProductInventory productInventory = null;
        try {
            ProductInventoryCacheRefreshRequest request = new ProductInventoryCacheRefreshRequest(productId, productInventoryService,false);
            requestAsyncProcessorService.process(request);
            long startTime = System.currentTimeMillis();
            long endTime = 0l;
            long waitTime = 0l;

            // 等待后台线程处理查询请求 查询请求处理完成后会更新缓存 不断从缓存中查询数据
            while (true) {
                if (waitTime >= 200) {
                    break;
                }
                productInventory = productInventoryService.getProductInventoryCache(productId);
                if (productInventory != null) {
                    return productInventory;
                } else {
                    Thread.sleep(20);
                    endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }
            // 200ms后还是未从缓存中查询到数据 直接查询数据库
            productInventory = productInventoryService.findProductInventory(productId);
            if (productInventory != null){
                request = new ProductInventoryCacheRefreshRequest(productId, productInventoryService,true);
                // 说明数据库有数据  再次发送读请求  这次读请求一定保证会被执行 不被过滤 刷新缓存
                requestAsyncProcessorService.process(request);
                return productInventory;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ProductInventory();
    }
}
