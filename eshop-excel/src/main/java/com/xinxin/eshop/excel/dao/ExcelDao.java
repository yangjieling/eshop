package com.xinxin.eshop.excel.dao;

import com.xinxin.eshop.excel.entity.ExcelHeadData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExcelDao {

    int saveAll(List<ExcelHeadData> dataList);
}
