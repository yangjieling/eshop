package com.xinxin.eshop.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;

public class ExcelHeadData {
    @ExcelProperty("商品编号")
    private Integer gid;
    @ExcelProperty("商品名称")
    private String gname;
    @ExcelProperty("商品价格")
    private Float gprice;
    @ExcelProperty("商品购买数量")
    private Integer buynum;
    @ExcelProperty("商品库存")
    private Integer gnum;
    @ExcelProperty("商品图片")
    private String gpic;

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public Float getGprice() {
        return gprice;
    }

    public void setGprice(Float gprice) {
        this.gprice = gprice;
    }

    public Integer getBuynum() {
        return buynum;
    }

    public void setBuynum(Integer buynum) {
        this.buynum = buynum;
    }

    public Integer getGnum() {
        return gnum;
    }

    public void setGnum(Integer gnum) {
        this.gnum = gnum;
    }

    public String getGpic() {
        return gpic;
    }

    public void setGpic(String gpic) {
        this.gpic = gpic;
    }
}
