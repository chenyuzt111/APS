package com.benewake.system.entity.kingdee;

import lombok.Data;

@Data
public class KingdeeProductionMaterial {



    //("产品编码")
    private String FMaterialID;
    //("产品编码")
    private String FMaterialName;

    //("生产订单编号")
    private String FMOBillNO;

    //("子项物料编码")
    private String FMaterialID2;

    private String FMaterialName1;

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
