package com.benewake.system.entity;

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
public class ApsRawMaterialBasicData implements Serializable {
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

    /**
     * 
     */
    @TableField(value = "f_material_property")
    private String fMaterialProperty;

    /**
     * 
     */
    @TableField(value = "f_material_group")
    private String fMaterialGroup;

    /**
     * 
     */
    @TableField(value = "f_procurement_lead_time")
    private String fProcurementLeadTime;

    /**
     * 
     */
    @TableField(value = "f_moq")
    private String fMoq;

    /**
     * 
     */
    @TableField(value = "f_mpq")
    private String fMpq;

    /**
     * 
     */
    @TableField(value = "f_safety_stock")
    private String fSafetyStock;

    /**
     * 
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}