package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.*;
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
    @TableField(value = "f_material_code",updateStrategy = FieldStrategy.IGNORED)
    private String fMaterialCode;

    /**
     * 
     */
    @TableField(value = "f_material_property",updateStrategy = FieldStrategy.IGNORED)
    private String fMaterialProperty;

    /**
     * 
     */
    @TableField(value = "f_material_group",updateStrategy = FieldStrategy.IGNORED)
    private String fMaterialGroup;

    /**
     * 
     */
    @TableField(value = "f_procurement_lead_time",updateStrategy = FieldStrategy.IGNORED)
    private Integer fProcurementLeadTime;

    /**
     * 
     */
    @TableField(value = "f_moq",updateStrategy = FieldStrategy.IGNORED)
    private Integer fMoq;

    /**
     * 
     */
    @TableField(value = "f_mpq",updateStrategy = FieldStrategy.IGNORED)
    private Integer fMpq;

    /**
     * 
     */
    @TableField(value = "f_safety_stock" ,updateStrategy = FieldStrategy.IGNORED)
    private Integer fSafetyStock;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}