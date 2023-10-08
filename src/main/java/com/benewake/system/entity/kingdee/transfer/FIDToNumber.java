package com.benewake.system.entity.kingdee.transfer;

import lombok.Data;

@Data
public class FIDToNumber {

    /**
     *
    @ExcelProperty("BOMID")
     */
    private String FID;

    /**
     *
    @ExcelProperty("BOM版本号")
     */
    private String FNumber;

}