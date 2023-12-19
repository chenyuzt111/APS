package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_material_shortage_analysis
 */
@TableName(value ="aps_material_shortage_analysis")
@Data
public class ApsMaterialShortageAnalysisDto implements Serializable {
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
    @TableField(value = "f_parent_material_code")
    private String fparentMaterialCode;

    /**
     * 
     */
    @TableField(value = "f_parent_material_name")
    private String fparentMaterialName;

    /**
     * 
     */
    @TableField(value = "f_process")
    private String fprocess;

    /**
     * 
     */
    @TableField(value = "f_sub_material_code")
    private String fsubMaterialCode;

    /**
     *
     */
    @TableField(value = "f_sub_material_name")
    private String fsubMaterialName;

    /**
     * 
     */
    @TableField(value = "f_sub_material_quantity")
    private String fsubMaterialQuantity;

    /**
     * 
     */
    @TableField(value = "f_sub_material_delivery_time")
    private String fsubMaterialDeliveryTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}