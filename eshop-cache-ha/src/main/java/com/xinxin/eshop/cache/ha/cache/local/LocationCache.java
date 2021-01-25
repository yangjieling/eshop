package com.xinxin.eshop.cache.ha.cache.local;

import java.util.HashMap;
import java.util.Map;

public class LocationCache {
    private static Map<Long, String> cityMap = new HashMap<>();

    static {
        cityMap.put(1L, "北京");
        cityMap.put(2L, "天津");
    }

    public static String getCityName(Long cityId) {
        return cityMap.get(cityId);
    }
}
