package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

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
    @TableField(value = "f_product_type")
    private String fProductType;

    /**
     * 
     */
    @TableField(value = "f_product_family")
    private String fProductFamily;

    /**
     * 
     */
    @TableField(value = "f_packaging_method")
    private String fPackagingMethod;

    /**
     * 
     */
    @TableField(value = "f_max_assembly_personnel")
    private String fMaxAssemblyPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_assembly_personnel")
    private String fMinAssemblyPersonnel;

    /**
     * 
     */
    @TableField(value = "f_max_testing_personnel")
    private String fMaxTestingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_testing_personnel")
    private String fMinTestingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_max_packaging_personnel")
    private String fMaxPackagingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_packaging_personnel")
    private String fMinPackagingPersonnel;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}