package com.xinxin.eshop.excel.service;

import com.xinxin.eshop.excel.entity.ExcelHeadData;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelService {

    public void downloadExcel(HttpServletResponse response);

    public void saveList(List<ExcelHeadData> dataList);
}
