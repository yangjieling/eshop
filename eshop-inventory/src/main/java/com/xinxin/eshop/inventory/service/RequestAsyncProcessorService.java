package com.xinxin.eshop.inventory.service;

import com.xinxin.eshop.inventory.request.Request;

public interface RequestAsyncProcessorService {

    void process(Request request);
}
