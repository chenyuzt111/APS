package com.benewake.system.KindDee;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class WeiUse {

    @ExcelProperty("产品编码")
    private String FMaterialID;

    @ExcelProperty("委外订单编号")
    private String FSubReqBillNO;

    @ExcelProperty("子项物料编码")
    private String FMaterialID2;

    @ExcelProperty("子项类型")
    private String FMaterialType;

    @ExcelProperty("应发数量")
    private String FMustQty;

    @ExcelProperty("已领数量")
    private String FPickedQty;

    @ExcelProperty("良品退料数量")
    private String FGoodReturnQty;

    @ExcelProperty("作业不良退料数量")
    private String FProcessDefectReturnQty;

/*    public WeiUse(String fMaterialID) {
        FMaterialID = fMaterialID;
    }*/
}
