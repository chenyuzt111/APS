package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 
 * @TableName aps_outsourced_material
 */
@TableName(value ="aps_outsourced_material")
@Data
public class ApsOutsourcedMaterialDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品编码
     */
    @ColumnWidth(14)
    @ExcelProperty("产品编码")
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;
    /**
     * 产品编码
     */
    @ColumnWidth(30)
    @ExcelProperty("产品名称")
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 委外订单编号
     */
    @ColumnWidth(18)
    @ExcelProperty("委外订单编号")
    @JsonProperty("subReqBillNo")
    @TableField(value = "f_sub_req_bill_no")
    private String fSubReqBillNo;

    /**
     * 子项物料编码
     */
    @ColumnWidth(18)
    @ExcelProperty("子项物料编码")
    @JsonProperty("materialId2")
    @TableField(value = "f_material_id2")
    private String fMaterialId2;
    /**
     * 子项物料编码
     */
    @ColumnWidth(24)
    @ExcelProperty("子项物料名称")
    @JsonProperty("materialName2")
    @TableField(value = "f_material_name2")
    private String fMaterialName2;

    /**
     * 子项类型
     */
    @ColumnWidth(12)
    @ExcelProperty("子项类型")
    @JsonProperty("materialType")
    @TableField(value = "f_material_type")
    private String fMaterialType;

    /**
     * 应发数量
     */
    @ColumnWidth(12)
    @ExcelProperty("应发数量")
    @JsonProperty("mustQty")
    @TableField(value = "f_must_qty")
    private Integer fMustQty;

    /**
     * 已领数量
     */
    @ColumnWidth(12)
    @ExcelProperty("已领数量")
    @JsonProperty("pickedQty")
    @TableField(value = "f_picked_qty")
    private Integer fPickedQty;

    /**
     * 良品退料数量
     */
    @ColumnWidth(18)
    @ExcelProperty("良品退料数量")
    @JsonProperty("goodReturnQty")
    @TableField(value = "f_good_return_qty")
    private Integer fGoodReturnQty;

    /**
     * 作业不良退料数量
     */
    @ColumnWidth(24)
    @ExcelProperty("作业不良退料数量")
    @JsonProperty("processDefectReturnQty")
    @TableField(value = "f_process_defect_return_qty")
    private Integer fProcessDefectReturnQty;

    /**
     * 版本号
     */
    @ColumnWidth(11)
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}