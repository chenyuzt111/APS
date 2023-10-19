package com.benewake.system.entity.mes;

import lombok.Data;

@Data
public class MesPcbaVersion {
    //("生产订单编号")
    private String productionOrderNumber;

    //("物料编码")
    private String materialCode;

    //("物料名称")
    private String materialName;

    //("本次分板完成数")
    private String burnInCompletionQuantity;

    //("分板合格数")
    private String BurnQualifiedCount;


    //("分板治具编号")
    private String BurnFixtureNumber;
}
