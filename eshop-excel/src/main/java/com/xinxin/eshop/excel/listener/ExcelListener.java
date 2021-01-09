package com.xinxin.eshop.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xinxin.eshop.excel.entity.ExcelHeadData;
import com.xinxin.eshop.excel.service.ExcelService;

import java.util.ArrayList;
import java.util.List;

public class ExcelListener extends AnalysisEventListener<ExcelHeadData> {

    private List<ExcelHeadData> dataList = new ArrayList<>();

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理dataList,方便内存回收
     */
    private static final int BATCH_COUNT = 3000;

    private ExcelService excelService;

    public ExcelListener(ExcelService excelService) {
        this.excelService = excelService;
    }

    /**
     * 每一条数据解析都会调用这个方法
     *
     * @param excelHeadData
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelHeadData excelHeadData, AnalysisContext analysisContext) {
        dataList.add(excelHeadData);
        if (dataList.size() >= BATCH_COUNT) {
            // 保存数据  数据入库
            saveData();
            // 一次保存完后清理掉dataList数据
            dataList.clear();
        }
    }

    /**
     * 所有数据解析完成后会调用该方法
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 再调用一次 确保最后一次数据也能入库
        if (dataList.size() > 0) {
            saveData();
        }
    }

    public void saveData() {
        excelService.saveList(dataList);
    }
}
