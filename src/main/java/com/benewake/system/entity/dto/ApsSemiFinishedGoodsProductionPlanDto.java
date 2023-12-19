package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
    private String fmaterialCode;


    @TableField(value = "f_material_name")
    private String fmaterialName;

    /**
     * 
     */
    @TableField(value = "f_quantity")
    private String fquantity;

    /**
     * 
     */
    @TableField(value = "f_start_time")
    private Date fstartTime;

    /**
     * 
     */
    @TableField(value = "f_required_delivery_time")
    private Date frequiredDeliveryTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}