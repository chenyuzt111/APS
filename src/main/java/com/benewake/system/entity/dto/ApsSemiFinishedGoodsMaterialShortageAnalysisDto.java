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
    private String fTaskId;

    /**
     * 
     */
    @TableField(value = "f_parent_code")
    private String fParentCode;

    /**
     * 
     */
    @TableField(value = "f_parent_name")
    private String fParentName;

    /**
     * 
     */
    @TableField(value = "f_process")
    private String fProcess;

    /**
     * 
     */
    @TableField(value = "f_semi_code")
    private String fSemiCode;
    /**
     *
     */
    @TableField(value = "f_semi_name")
    private String fSemiName;

    /**
     * 
     */
    @TableField(value = "f_semi_qty")
    private String fSemiQty;

    /**
     * 
     */
    @TableField(value = "f_sub_code")
    private String fSubCode;
    /**
     *
     */
    @TableField(value = "f_sub_name")
    private String fSubName;

    /**
     * 
     */
    @TableField(value = "f_sub_qty")
    private String fSubQty;

    /**
     * 
     */
    @TableField(value = "f_sub_delivery_time")
    private String fSubDeliveryTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}