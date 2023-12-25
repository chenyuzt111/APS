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
 * @TableName aps_material_shortage_analysis
 */
@TableName(value ="aps_material_shortage_analysis")
@Data
public class ApsMaterialShortageAnalysisDto implements Serializable {
    /**
     * 
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @ExcelProperty("任务号")
    @TableField(value = "f_task_id")
    private String ftaskId;

    /**
     * 
     */
    @ExcelProperty("父物料编码")
    @TableField(value = "f_parent_material_code")
    private String fparentMaterialCode;

    /**
     * 
     */
    @ExcelProperty("父物料名称")
    @TableField(value = "f_parent_material_name")
    private String fparentMaterialName;

    /**
     * 
     */
    @ExcelProperty("工序")
    @TableField(value = "f_process")
    private String fprocess;

    /**
     * 
     */
    @ExcelProperty("子物料编码")
    @TableField(value = "f_sub_material_code")
    private String fsubMaterialCode;

    /**
     *
     */
    @ExcelProperty("子物料名称")
    @TableField(value = "f_sub_material_name")
    private String fsubMaterialName;

    /**
     * 
     */
    @ExcelProperty("子物料所需数量")
    @TableField(value = "f_sub_material_quantity")
    private String fsubMaterialQuantity;

    /**
     *
     */
    @ExcelProperty("子物料有货时间")
    @TableField(value = "f_sub_material_delivery_time")
    private String fsubMaterialDeliveryTime;


    @ExcelProperty("版本号")
    private String chVersion;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}