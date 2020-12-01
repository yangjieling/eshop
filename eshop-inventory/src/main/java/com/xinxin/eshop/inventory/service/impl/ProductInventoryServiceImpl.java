package com.xinxin.eshop.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.xinxin.eshop.inventory.dao.RedisDao;
import com.xinxin.eshop.inventory.mapper.ProductInventoryMapper;
import com.xinxin.eshop.inventory.model.ProductInventory;
import com.xinxin.eshop.inventory.service.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Autowired
    private ProductInventoryMapper productInventoryMapper;

    @Autowired
    private RedisDao redisDao;

    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        productInventoryMapper.updateProductInventory(productInventory);
    }

    @Override
    public ProductInventory findProductInventory(Integer productId) {
        return productInventoryMapper.findProductInventory(productId);
    }

    @Override
    public void setProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:" + productInventory.getProductId();
        redisDao.set(key, String.valueOf(productInventory.getProductInventoryCnt()));
    }

    @Override
    public ProductInventory getProductInventoryCache(Integer productId) {
        String key = "product:inventory:" + productId;
        String value = redisDao.getValue(key);
        if (!StrUtil.isBlank(value) && !"null".equals(value)) {
            return new ProductInventory(productId, Integer.valueOf(value));
        }
        return null;
    }

    @Override
    public void removeProductInventoryCache(Integer productId) {
        String key = "product:inventory:" + productId;
        redisDao.delete(key);
    }
}