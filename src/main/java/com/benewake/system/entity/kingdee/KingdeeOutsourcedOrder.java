package com.benewake.system.entity.kingdee;


import lombok.Data;
/*
* 委外订单列表数据类型*/
@Data
public class KingdeeOutsourcedOrder {
    //("单据编号")
    private String FBillNo;

    //("单据类型")
    private String FBillType;
    //("单据类型")
    private String FBILLTYPEID;


    //("物料编码")
    private String FMaterialId;

    //("数量")
    private String FQty;

    //("业务状态")
    private String FStatus;

    //("领料状态")
    private String FPickMtrlStatus;

    //("入库数量")
    private String FStockInQty;

    //("BOM版本")
    private String FBomId;



    // Constructors, getters, setters, and other methods...
}
