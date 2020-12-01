package com.xinxin.eshop.inventory.model;

/**
 * 商铺库存对象
 */
public class ProductInventory {
    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 商品库存数量
     */
    private Integer productInventoryCnt;

    public ProductInventory() {
    }

    public ProductInventory(Integer productId, Integer productInventoryCnt) {
        this.productId = productId;
        this.productInventoryCnt = productInventoryCnt;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductInventoryCnt() {
        return productInventoryCnt;
    }

    public void setProductInventoryCnt(Integer productInventoryCnt) {
        this.productInventoryCnt = productInventoryCnt;
    }
}
