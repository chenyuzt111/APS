package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_finished_product_basic_data
 */
@TableName(value ="aps_finished_product_basic_data")
@Data
public class ExcelFinishedProductTemplate implements Serializable {

    /**
     * 
     */
    @ExcelProperty("物料编码")
    @TableField(value = "f_material_code")
    private String materialCode;



    /**
     * 
     */
    @ExcelProperty("物料属性")
    @TableField(value = "f_material_property")
    private String materialProperty;

    /**
     * 
     */
    @ExcelProperty("物料分组")
    @TableField(value = "f_material_group")
    private String materialGroup;

    /**
     * 
     */
    @ExcelProperty("产品类型")
    @TableField(value = "f_product_type")
    private String productType;

    /**
     * 
     */
    @ExcelProperty("产品族")
    @TableField(value = "f_product_family")
    private String productFamily;

    /**
     * 
     */
    @ExcelProperty("包装方式")
    @TableField(value = "f_packaging_method")
    private String packagingMethod;

    /**
     * 
     */
    @ExcelProperty("组装人数MAX")
    @TableField(value = "f_max_assembly_personnel")
    private String maxAssemblyPersonnel;

    /**
     * 
     */
    @ExcelProperty("组装人数MIN")
    @TableField(value = "f_min_assembly_personnel")
    private String minAssemblyPersonnel;

    /**
     *
     */
    @ExcelProperty("测试人数MAX")
    @TableField(value = "f_max_testing_personnel")
    private String maxTestingPersonnel;

    /**
     * 
     */
    @ExcelProperty("测试人数MIN")
    @TableField(value = "f_min_testing_personnel")
    private String minTestingPersonnel;

    /**
     *
     */
    @ExcelProperty("包装人数MAX")
    @TableField(value = "f_max_packaging_personnel")
    private String maxPackagingPersonnel;

    /**
     * 
     */
    @ExcelProperty("包装人数MIN")
    @TableField(value = "f_min_packaging_personnel")
    private String minPackagingPersonnel;

    /**
     * 
     */
    @ExcelProperty("MOQ")
    @TableField(value = "f_moq")
    private String moq;

    /**
     * 
     */
    @ExcelProperty("MPQ")
    @TableField(value = "f_mpq")
    private String mpq;

    /**
     * 
     */
    @ExcelProperty("安全库存")
    @TableField(value = "f_safety_stock")
    private String safetyStock;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}