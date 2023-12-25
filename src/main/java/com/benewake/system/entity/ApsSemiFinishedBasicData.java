package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName aps_semi_finished_basic_data
 */
@TableName(value ="aps_semi_finished_basic_data")
@Data
public class ApsSemiFinishedBasicData implements Serializable {
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
    @TableField(value = "f_product_type",updateStrategy = FieldStrategy.IGNORED)
    private String fProductType;

    /**
     *
     */
    @TableField(value = "f_procurement_lead_time",updateStrategy = FieldStrategy.IGNORED)
    private String fProcurementLeadTime;

    /**
     *
     */
    @TableField(value = "f_moq",updateStrategy = FieldStrategy.IGNORED)
    private String fMoq;

    /**
     *
     */
    @TableField(value = "f_mpq",updateStrategy = FieldStrategy.IGNORED)
    private String fMpq;

    /**
     *
     */
    @TableField(value = "f_safety_stock",updateStrategy = FieldStrategy.IGNORED)
    private String fSafetyStock;


    @TableField(exist = false,updateStrategy = FieldStrategy.IGNORED)
    private static final long serialVersionUID = 1L;
}