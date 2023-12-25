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
 * @TableName aps_semi_finished_goods_material_shortage_analysis
 */
@TableName(value ="aps_semi_finished_goods_material_shortage_analysis")
@Data
public class ApsSemiFinishedGoodsMaterialShortageAnalysisDto implements Serializable {
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
    @TableField(value = "f_parent_code")
    private String fparentCode;

    /**
     * 
     */
    @ExcelProperty("父物料名称")
    @TableField(value = "f_parent_name")
    private String fparentName;

    /**
     * 
     */
    @ExcelProperty("工序")
    @TableField(value = "f_process")
    private String fprocess;

    /**
     * 
     */
    @ExcelProperty("半成品编码")
    @TableField(value = "f_semi_code")
    private String fsemiCode;
    /**
     *
     */
    @ExcelProperty("半成品名称")
    @TableField(value = "f_semi_name")
    private String fsemiName;

    /**
     * 
     */
    @ExcelProperty("半成品所需数量")
    @TableField(value = "f_semi_qty")
    private String fsemiQty;

    /**
     * 
     */
    @ExcelProperty("子物料编码")
    @TableField(value = "f_sub_code")
    private String fsubCode;
    /**
     *
     */
    @ExcelProperty("子物料名称")
    @TableField(value = "f_sub_name")
    private String fsubName;

    /**
     * 
     */
    @ExcelProperty("子物料所需数量")
    @TableField(value = "f_sub_qty")
    private String fsubQty;

    /**
     * 
     */
    @ExcelProperty("子物料有货时间")
    @TableField(value = "f_sub_delivery_time")
    private String fsubDeliveryTime;


    @ExcelProperty("版本号")
    private String chVersion;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}