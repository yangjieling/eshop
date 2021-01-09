package com.xinxin.eshop.excel.utils;

import com.alibaba.excel.EasyExcel;
import com.xinxin.eshop.excel.entity.ExcelHead;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * excel导入导出工具类
 */
public class ExcelUtil {

    /**
     * 导出excel
     * @param response 输出对象
     * @param t sheet表头
     * @param list sheet数据
     * @throws IOException
     */
    public static void download(HttpServletResponse response,Class t,List list) throws IOException {
        response.setContentType("application/vnd.ms-excel");// 设置文本内省
        response.setCharacterEncoding("utf-8");// 设置字符编码
        response.setHeader("Content-disposition", "attachment;filename=demo.xlsx"); // 设置响应头
        EasyExcel.write(response.getOutputStream(), t).sheet("模板").doWrite(list); //用io流来写入数据
    }
























    public static List<ExcelHead> dataList = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            ExcelHead data = new ExcelHead();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            dataList.add(data);
        }
    }

    public static void outPutExcel() {
        String fileName = "D:\\workfile\\" + " EasyExcel.xlsx";
        EasyExcel.write(fileName, ExcelHead.class).sheet("模板").doWrite(dataList);
    }

    public static void main(String[] args) {
        outPutExcel();
    }
}
