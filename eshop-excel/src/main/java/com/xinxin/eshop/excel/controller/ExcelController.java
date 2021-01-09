package com.xinxin.eshop.excel.controller;

import com.alibaba.excel.EasyExcel;
import com.xinxin.eshop.excel.entity.ExcelHeadData;
import com.xinxin.eshop.excel.listener.ExcelListener;
import com.xinxin.eshop.excel.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @RequestMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response){
        excelService.downloadExcel(response);
    }


    @RequestMapping("/importExcel")
    @ResponseBody
    public String importExcel(@RequestParam(value = "excelFile")MultipartFile file){
        try {
            EasyExcel.read(file.getInputStream(), ExcelHeadData.class,new ExcelListener(excelService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            return "数据导入失败，请重新导入";
        }
        return "数据导入成功";
    }
}
