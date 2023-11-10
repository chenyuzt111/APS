package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_material_shortage_analysis
 */
@TableName(value ="aps_material_shortage_analysis")
@Data
public class ApsMaterialShortageAnalysis implements Serializable {
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
    @TableField(value = "f_parent_material_code")
    private String fParentMaterialCode;


    /**
     * 
     */
    @TableField(value = "f_process")
    private String fProcess;

    /**
     * 
     */
    @TableField(value = "f_sub_material_code")
    private String fSubMaterialCode;

    /**
     * 
     */
    @TableField(value = "f_sub_material_quantity")
    private String fSubMaterialQuantity;

    /**
     * 
     */
    @TableField(value = "f_sub_material_delivery_time")
    private String fSubMaterialDeliveryTime;

    /**
     * 
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}