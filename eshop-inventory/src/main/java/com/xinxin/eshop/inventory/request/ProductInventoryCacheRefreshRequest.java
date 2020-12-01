package com.xinxin.eshop.inventory.request;

import com.xinxin.eshop.inventory.model.ProductInventory;
import com.xinxin.eshop.inventory.service.ProductInventoryService;

/**
 * 更新redis缓存中的商铺库存信息
 */
public class ProductInventoryCacheRefreshRequest implements Request {

    private Integer productId;

    private ProductInventoryService productInventoryService;

    /**
     * 是否强制刷新缓存标志
     * false：不强制刷新
     * true：强制刷新，请求一定会被后台线程执行
     */
    private boolean forceRefresh;

    public ProductInventoryCacheRefreshRequest(Integer productId,
                                               ProductInventoryService productInventoryService,
                                               boolean forceRefresh){
        this.productId = productId;
        this.productInventoryService = productInventoryService;
        this.forceRefresh = forceRefresh;
    }

    @Override
    public void process() {
        // 从数据库中查询最新的商品库存数量
        ProductInventory productInventory = productInventoryService.findProductInventory(productId);
        // 将最新的商品库存数量 刷新到redis中去
        productInventoryService.setProductInventoryCache(productInventory);
    }

    @Override
    public Integer getProductId() {
        return productId;
    }

    @Override
    public boolean isForceReFresh() {
        return forceRefresh;
    }

}
