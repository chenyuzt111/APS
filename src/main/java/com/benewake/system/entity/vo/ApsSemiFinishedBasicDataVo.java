package com.benewake.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_semi_finished_basic_data
 */
@TableName(value ="aps_semi_finished_basic_data")
@Data
public class ApsSemiFinishedBasicDataVo implements Serializable {
    /**
     * 
     */
    @ExcelIgnore
    private Integer id;

    /**
     * 
     */
    @ExcelProperty("物料编码")
    private String materialCode;

    /**
     *
     */
    @ExcelProperty("物料名称")
    private String materialName;

    /**
     * 
     */
    @ExcelProperty("物料属性")
    private String materialProperty;

    /**
     * 
     */
    @ExcelProperty("物料分组")
    private String materialGroup;

    /**
     * 
     */
    @ExcelProperty("产品类型")
    private String productType;

    /**
     * 
     */
    @ExcelProperty("采购周期")
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