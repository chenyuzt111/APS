package com.benewake.system.entity.mes;

import lombok.Data;

@Data
public class MesCalibrationTests {

    //("生产订单编号")
    private String productionOrderNumber;

    //("物料编码")
    private String materialCode;

    //("物料名称")
    private String materialName;

    //("本次校准测试完成数")
    private String burnInCompletionQuantity;

    //("校准合格数")
    private String BurnQualifiedCount;
    private String UnBurnQualifiedCount;


    //("测试工装编号")
    private String BurnFixtureNumber;

    //("订单总数")
    private String totalNumber;
}
