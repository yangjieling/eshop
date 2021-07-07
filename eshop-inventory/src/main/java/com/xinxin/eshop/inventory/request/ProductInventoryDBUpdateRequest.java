package com.xinxin.eshop.inventory.request;

import com.xinxin.eshop.inventory.model.ProductInventory;
import com.xinxin.eshop.inventory.service.ProductInventoryService;

/**
 * @ClassName ProductInventoryDBUpdateRequest
 * @Description 更新数据库商品库存数量请求封装对象
 * @Author lantianbaiyun
 * @Date 2021-07-07
 * @Version 1.0
 */
public class ProductInventoryDBUpdateRequest implements Request {

    private ProductInventory productInventory;

    private ProductInventoryService productInventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory productInventory,
                                           ProductInventoryService productInventoryService) {
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void process() {
        // 删除redis中商品库存缓存
        productInventoryService.removeProductInventoryCache(productInventory.getProductId());
        // 更新数据库中商品库存
        productInventoryService.updateProductInventory(productInventory);
    }

    @Override
    public Integer getProductId() {
        return productInventory.getProductId();
    }

    @Override
    public boolean isForceReFresh() {
        return false;
    }
}
