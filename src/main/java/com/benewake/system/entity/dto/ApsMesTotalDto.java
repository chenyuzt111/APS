package com.benewake.system.entity.dto;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName aps_mes_total
 */
@TableName(value ="aps_mes_total")
@Data
public  class ApsMesTotalDto implements Serializable {
    /**
     * 唯一标识符
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
