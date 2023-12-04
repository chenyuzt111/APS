package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_finished_product_basic_data
 */
@TableName(value ="aps_finished_product_basic_data")
@Data
public class ApsFinishedProductBasicDataParam implements Serializable {
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
    @TableField(value = "f_product_type")
    private String productType;

    /**
     * 
     */
    @TableField(value = "f_product_family")
    private String productFamily;

    /**
     * 
     */
    @TableField(value = "f_packaging_method")
    private String packagingMethod;

    /**
     * 
     */
    @TableField(value = "f_max_assembly_personnel")
    private String maxAssemblyPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_assembly_personnel")
    private String minAssemblyPersonnel;

    /**
     * 
     */
    @TableField(value = "f_max_testing_personnel")
    private String maxTestingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_testing_personnel")
    private String minTestingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_max_packaging_personnel")
    private String maxPackagingPersonnel;

    /**
     * 
     */
    @TableField(value = "f_min_packaging_personnel")
    private String minPackagingPersonnel;

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