package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_raw_material_basic_data
 */
@TableName(value ="aps_raw_material_basic_data")
@Data
public class ExcelRawMaterialBasicDataTemplate implements Serializable {


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
    @ExcelProperty("采购周期")
    @TableField(value = "f_procurement_lead_time")
    private Integer procurementLeadTime;

    /**
     * 
     */
    @ExcelProperty("MOQ")
    @TableField(value = "f_moq")
    private Integer moq;

    /**
     * 
     */
    @ExcelProperty("MPQ")
    @TableField(value = "f_mpq")
    private Integer mpq;

    /**
     * 
     */
    @ExcelProperty("安全库存")
    @TableField(value = "f_safety_stock")
    private Integer safetyStock;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}