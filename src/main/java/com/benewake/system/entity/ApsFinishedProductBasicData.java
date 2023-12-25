package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_finished_product_basic_data
 */
@TableName(value ="aps_finished_product_basic_data")
@Data
public class ApsFinishedProductBasicData implements Serializable {
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
    @TableField(value = "f_product_family",updateStrategy = FieldStrategy.IGNORED)
    private String fProductFamily;

    /**
     * 
     */
    @TableField(value = "f_packaging_method",updateStrategy = FieldStrategy.IGNORED)
    private String fPackagingMethod;

    /**
     * 
     */
    @TableField(value = "f_max_assembly_personnel",updateStrategy = FieldStrategy.IGNORED)
    private String fMaxAssemblyPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_assembly_personnel",updateStrategy = FieldStrategy.IGNORED)
    private String fMinAssemblyPersonnel;

    /**
     * 
     */
    @TableField(value = "f_max_testing_personnel",updateStrategy = FieldStrategy.IGNORED)
    private String fMaxTestingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_testing_personnel",updateStrategy = FieldStrategy.IGNORED)
    private String fMinTestingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_max_packaging_personnel",updateStrategy = FieldStrategy.IGNORED)
    private String fMaxPackagingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_packaging_personnel",updateStrategy = FieldStrategy.IGNORED)
    private String fMinPackagingPersonnel;

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