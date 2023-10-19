package com.benewake.system.entity.mes;

import lombok.Data;

@Data
public class MesPcbaBurn {
    //"生产订单编号")
    private String productionOrderNumber;

    //"物料编码")
    private String materialCode;

    //"物料名称")
    private String materialName;

    //"本次烧录完成数")
    private String burnInCompletionQuantity;

    //"烧录合格数")
    private String BurnQualifiedCount;


    //"烧录工装编号")
    private String BurnFixtureNumber;

}