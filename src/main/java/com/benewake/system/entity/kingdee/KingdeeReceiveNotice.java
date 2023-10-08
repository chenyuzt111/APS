package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeeReceiveNotice {

    //("单据编号")
    private String FBillNo;
    //("物料编码")
    private String FMaterialId;

    //("实收数量")
    private String FMustQty;

    //("检验数量")
    private String FCheckQty;

    //("合格数量")
    private String FReceiveQty;

    //("让步接收数量(基本单位)")
    private String FCsnReceiveBaseQty;

    //("入库数量")
    private String FInStockQty;


}