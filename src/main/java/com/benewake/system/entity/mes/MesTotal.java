package com.benewake.system.entity.mes;

import lombok.Data;

@Data
public class MesTotal {



    /**
     * 生产订单编号
     */
    private String productionOrderNumber;

    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 订单总数
     */
    private String totalNumber;

    /**
     * 本次完成数
     */
    private String burnInCompletionQuantity;

    /**
     * 合格数
     */
    private String burnQualifiedCount;

    /**
     * 不合格数
     */
    private String unBurnQualifiedCount;

    /**
     * 工装编号
     */
    private String burnFixtureNumber;

    /**
     * 机器id
     */
    private Integer burnFixtureId;

    /**
     * 工序
     */
    private String process;

    /**
     * 工件
     */
    private String workpiece;

    /**
     * 版本号
     */
    private Integer version;
}
