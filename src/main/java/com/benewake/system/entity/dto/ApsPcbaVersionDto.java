package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_pcba_version
 */
@TableName(value ="aps_pcba_version")
@Data
public class ApsPcbaVersionDto implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
     * 本次分板完成数
     */
    @TableField(value = "burn_in_completion_quantity")
    private String burnInCompletionQuantity;

    /**
     * 分板合格数
     */
    @TableField(value = "burn_qualified_count")
    private String burnQualifiedCount;

    /**
     * 分板不合格数
     */
    @TableField(value = "un_burn_qualified_count")
    private String unburnQualifiedCount;

    /**
     * 分板治具编号
     */
    @TableField(value = "burn_fixture_number")
    private String burnFixtureNumber;


    /**
     * 订单总数
     */
    @TableField(value = "total_number")
    private String totalNumber;


    /**
     *
     */
    @TableField(value = "burn_fixture_id")
    private Integer burnFixtureId;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}