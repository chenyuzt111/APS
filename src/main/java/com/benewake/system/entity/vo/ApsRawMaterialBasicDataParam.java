package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_raw_material_basic_data
 */
@TableName(value ="aps_raw_material_basic_data")
@Data
public class ApsRawMaterialBasicDataParam implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_material_code")
    private String materialCode;


    /**
     * 
     */
    @TableField(value = "f_material_property")
    private String materialProperty;

    /**
     * 
     */
    @TableField(value = "f_material_group")
    private String materialGroup;

    /**
     * 
     */
    @TableField(value = "f_procurement_lead_time")
    private String procurementLeadTime;

    /**
     * 
     */
    @TableField(value = "f_moq")
    private String moq;

    /**
     * 
     */
    @TableField(value = "f_mpq")
    private String mpq;

    /**
     * 
     */
    @TableField(value = "f_safety_stock")
    private String safetyStock;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}