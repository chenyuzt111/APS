package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_semi_finished_goods_material_shortage_analysis
 */
@TableName(value ="aps_semi_finished_goods_material_shortage_analysis")
@Data
public class ApsSemiFinishedGoodsMaterialShortageAnalysisDto implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_task_id")
    private String ftaskId;

    /**
     * 
     */
    @TableField(value = "f_parent_code")
    private String fparentCode;

    /**
     * 
     */
    @TableField(value = "f_parent_name")
    private String fparentName;

    /**
     * 
     */
    @TableField(value = "f_process")
    private String fprocess;

    /**
     * 
     */
    @TableField(value = "f_semi_code")
    private String fsemiCode;
    /**
     *
     */
    @TableField(value = "f_semi_name")
    private String fsemiName;

    /**
     * 
     */
    @TableField(value = "f_semi_qty")
    private String fsemiQty;

    /**
     * 
     */
    @TableField(value = "f_sub_code")
    private String fsubCode;
    /**
     *
     */
    @TableField(value = "f_sub_name")
    private String fsubName;

    /**
     * 
     */
    @TableField(value = "f_sub_qty")
    private String fsubQty;

    /**
     * 
     */
    @TableField(value = "f_sub_delivery_time")
    private String fsubDeliveryTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}