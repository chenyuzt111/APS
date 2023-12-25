package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
public class ApsRawMaterialBasicDataDto implements Serializable {
    /**
     * 
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @ExcelProperty("物料编码")
    @TableField(value = "f_material_code")
    private String materialCode;

    /**
     *
     */
    @ExcelProperty("物料名称")
    @TableField(value = "f_material_name")
    private String materialName;

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
    private String procurementLeadTime;

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