package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class shoprequestDD {

    /**
     *
    @ExcelProperty("物料编码")
     */
    private String FMaterialId;

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
