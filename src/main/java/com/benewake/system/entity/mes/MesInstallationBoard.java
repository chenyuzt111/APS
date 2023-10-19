package com.benewake.system.entity.mes;

import lombok.Data;

@Data
public class MesInstallationBoard {

    //("生产订单编号")
    private String productionOrderNumber;

    //("物料编码")
    private String materialCode;

    //("物料名称")
    private String materialName;

    //("本次安装完成数")
    private String burnInCompletionQuantity;

    //("安装合格数")
    private String BurnQualifiedCount;



}
