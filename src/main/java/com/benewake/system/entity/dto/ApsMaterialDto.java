package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_material
 */
@TableName(value ="aps_material")
@Data
public class ApsMaterialDto implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品编码
     */
    @ExcelProperty("产品编码")
    private String materialId;

    /**
     * 物料名称
     */
    @ExcelProperty("产品名称")
    private String materialName;

    /**
     * 委外订单编号
     */
    @ExcelProperty("订单编号")
    private String subReqBillNo;

    /**
     * 子项物料编码
     */
    @ExcelProperty("子项物料编码")
    private String materialId2;

    /**
     * 子项物料名称
     */
    @ExcelProperty("子项物料名称")
    private String materialName2;

    /**
     * 子项类型
     */
    @ExcelProperty("子项类型")
    private String materialType;

    /**
     * 应发数量
     */
    @ExcelProperty("应发数量")
    private String mustQty;

    /**
     * 已领数量
     */
    @ExcelProperty("已领数量")
    private String pickedQty;

    /**
     * 良品退料数量
     */
    @ExcelProperty("良品退料数量")
    private String goodReturnQty;

    /**
     * 作业不良退料数量
     */
    @ExcelProperty("作业不良退料数量")
    private String processDefectReturnQty;

    /**
     * 定制物料编码
     */
    @ExcelProperty("定制物料编码")
    private String dzmaterialId;

    /**
     * 表单名称
     */
    @ExcelProperty("表单名称")
    private String formName;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}