package com.xinxin.eshop.inventory.service;

import com.xinxin.eshop.inventory.model.ProductInventory;

public interface ProductInventoryService {

    /**
     * 更新商品库存数据库
     * @param productInventory
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 根据商品ID查询商品库存信息
     * @param productId
     * @return
     */
    ProductInventory findProductInventory(Integer productId);

    /**
     * 添加商品库存缓存
     * @param productInventory
     */
    void setProductInventoryCache(ProductInventory productInventory);

    /**
     * 获取商品库存缓存
     * @param productId
     * @return
     */
    ProductInventory getProductInventoryCache(Integer productId);

    /**
     * 删除商品库存
     * @param productId
     */
    void removeProductInventoryCache(Integer productId);
}
