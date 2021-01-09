package com.xinxin.eshop.excel.service.impl;

import com.xinxin.eshop.excel.dao.ExcelDao;
import com.xinxin.eshop.excel.entity.ExcelHeadData;
import com.xinxin.eshop.excel.service.ExcelService;
import com.xinxin.eshop.excel.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private ExcelDao excelDao;

    @Override
    public void downloadExcel(HttpServletResponse response) {
        // 模拟通过dao从数据库中查询出数据
        List<ExcelHeadData> dataList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            ExcelHeadData data = new ExcelHeadData();
            data.setGid(Integer.valueOf(i));
            data.setGname("商品名称"+i);
            data.setBuynum(Integer.valueOf(i));
            data.setGnum(Integer.valueOf(100+i));
            data.setGpic("www.baidu.com/"+i+".jpg");
            data.setGprice(Float.valueOf(i));

            dataList.add(data);
        }

        try {
            ExcelUtil.download(response, ExcelHeadData.class, dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveList(List<ExcelHeadData> dataList) {
        System.out.println("开始保存数据");
        excelDao.saveAll(dataList);
    }
}
