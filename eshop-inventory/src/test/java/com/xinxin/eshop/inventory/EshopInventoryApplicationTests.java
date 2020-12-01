package com.xinxin.eshop.inventory;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class EshopInventoryApplicationTests {

    public static void main(String[] args) throws InterruptedException {
        String str = "91,82,73";
        List<String> params = Arrays.asList(str.split(","));
        String sql = "select * from store_item where itemId in ";
        String jpql = "";
        for (int i = 0; i < params.size(); i++) {
            String s = "select storeId from store_item where itemId = " + params.get(i);

        }
    }

    @Test
    void contextLoads() {
    }

}
