package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeePurchaseRequest {

    /**
     *
    @ExcelProperty("单据编号")
     */
    private String FBillNo;
    /**
     *
    @ExcelProperty("物料编码")
     */
    private String FMaterialId;
    /**
     *
    @ExcelProperty("物料编码")
     */
    private String FMaterialName;

    /**
     *
    @ExcelProperty("批准数量")
     */
    private String FBaseUnitQty;

    /**
     *
    @ExcelProperty("到货日期")
     */
    private String FArrivalDate;
}
