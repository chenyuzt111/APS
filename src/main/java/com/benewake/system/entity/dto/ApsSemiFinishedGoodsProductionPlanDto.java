package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_semi_finished_goods_production_plan
 */
@TableName(value ="aps_semi_finished_goods_production_plan")
@Data
public class ApsSemiFinishedGoodsProductionPlanDto implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_material_code")
    private String fMaterialCode;


    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 
     */
    @TableField(value = "f_quantity")
    private String fQuantity;

    /**
     * 
     */
    @TableField(value = "f_start_time")
    private String fStartTime;

    /**
     * 
     */
    @TableField(value = "f_required_delivery_time")
    private String fRequiredDeliveryTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}