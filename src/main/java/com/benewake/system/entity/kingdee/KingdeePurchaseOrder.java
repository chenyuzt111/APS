package com.benewake.system.entity.kingdee;

import lombok.Data;

/*
* 采购订单数据类型
* */
@Data
public class KingdeePurchaseOrder {


    //("单据编号")
    private String FBillNo;
    //("物料编码")
    private String FMaterialId;

    private String FMaterialName;
    //("剩余收料数量")
    private String FRemainReceiveQty;
    //("交货日期")
    private String FDeliveryDate_Plan;


    /*//("剩余未出数量（销售基本）")
    private String FBaseRemainOutQty;*/



}