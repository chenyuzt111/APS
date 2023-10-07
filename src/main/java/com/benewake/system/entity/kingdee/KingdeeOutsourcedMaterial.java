package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeeOutsourcedMaterial {

    //("产品编码")
    private String FMaterialID;

    //("委外订单编号")
    private String FSubReqBillNO;

    //("子项物料编码")
    private String FMaterialID2;

    //("子项类型")
    private String FMaterialType;

    //("应发数量")
    private String FMustQty;

    //("已领数量")
    private String FPickedQty;

    //("良品退料数量")
    private String FGoodReturnQty;

    //("作业不良退料数量")
    private String FProcessDefectReturnQty;

}
