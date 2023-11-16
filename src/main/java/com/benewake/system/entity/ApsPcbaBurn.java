package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_pcba_burn
 */
@TableName(value ="aps_pcba_burn")
@Data
public class ApsPcbaBurn implements Serializable {
    /**
     * 唯一标识符
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 生产订单编号
     */
    @TableField(value = "production_order_number")
    private String productionOrderNumber;

    /**
     * 物料编码
     */
    @TableField(value = "material_code")
    private String materialCode;

    /**
     * 物料名称
     */
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 本次烧录完成数
     */
    @TableField(value = "burn_in_completion_quantity")
    private String burnInCompletionQuantity;

    /**
     * 烧录合格数
     */
    @TableField(value = "burn_qualified_count")
    private String burnQualifiedCount;

    /**
     * 烧录不合格数
     */
    @TableField(value = "un_burn_qualified_count")
    private String unburnQualifiedCount;

    /**
     * 烧录工装编号
     */
    @TableField(value = "burn_fixture_number")
    private String burnFixtureNumber;

    /**
     * 订单总数
     */
    @TableField(value = "total_number")
    private String totalNumber;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}