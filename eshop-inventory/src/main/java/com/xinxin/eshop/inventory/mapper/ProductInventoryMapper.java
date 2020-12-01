package com.xinxin.eshop.inventory.mapper;

import com.xinxin.eshop.inventory.model.ProductInventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductInventoryMapper {

    /**
     * 更新商品库存
     * @param productInventory
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 查询商品库存
     * @param productId 商品ID
     * @return
     */
    ProductInventory findProductInventory(@Param("productId") Integer productId);
}
