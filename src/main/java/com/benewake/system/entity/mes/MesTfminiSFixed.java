package com.benewake.system.entity.mes;


import lombok.Data;




@Data
public class MesTfminiSFixed  {

    //"生产订单编号")
    private String productionOrderNumber;

    //"物料编码")
    private String materialCode;

    //"物料名称")
    private String materialName;

    /**
     * 本次安装完成数
     */
    private String burnInCompletionQuantity;

    /**
     * 安装合格数84950991
     */
    private String BurnQualifiedCount;

    /**
     * 用料清单编号
     */
    private String BurnFixtureNumber;

    /**
     * 机器id
     */
    private Integer burnFixtureId;

    /**
     * 订单总数
     */
    private String totalNumber;

    /**
     * 安装不合格数84950990
     */
    private String UnBurnQualifiedCount;

    /**
     * 版本号
     */
    private Integer version;










}