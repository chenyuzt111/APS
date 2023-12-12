package com.benewake.system.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="aps_material_bom")
@Data
public class ApsMaterialBom implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父项物料编码
     */
    @ColumnWidth(20)
    @ExcelProperty("父项物料编码")
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;



    /**
     * 数据状态
     */
    @ColumnWidth(15) // 设置列宽为15
    @ExcelProperty("数据状态")
    @JsonProperty("documentStatus")
    @TableField(value = "f_document_status")
    private String fDocumentStatus;

    /**
     * 子项物料编码
     */
    @ColumnWidth(20) // 设置列宽为20
    @ExcelProperty("子项物料编码")
    @JsonProperty("materialIdChild")
    @TableField(value = "f_material_id_child")
    private String fMaterialIdChild;


    /**
     * 项次
     */
    @ColumnWidth(10) // 设置列宽
    @ExcelProperty("项次")
    @JsonProperty("replaceGroupBop")
    @TableField(value = "f_replace_group_bop")
    private String fReplaceGroupBop;

    /**
     * 用量:分子
     */
    @ColumnWidth(15) // 设置列宽
    @ExcelProperty("用量:分子")
    @JsonProperty("numerator")
    @TableField(value = "f_numerator")
    private String fNumerator;

    /**
     * 用量:分母
     */
    @ColumnWidth(15) // 设置列宽
    @ExcelProperty("用量:分母")
    @JsonProperty("denominator")
    @TableField(value = "f_denominator")
    private String fDenominator;

    /**
     * 固定损耗
     */
    @ColumnWidth(12) // 设置列宽
    @ExcelProperty("固定损耗")
    @JsonProperty("fixScrapQtyLot")
    @TableField(value = "f_fix_scrap_qty_lot")
    private String fFixScrapQtyLot;

    /**
     * 子项类型
     */
    @ColumnWidth(12) // 设置列宽
    @ExcelProperty("子项类型")
    @JsonProperty("materialType")
    @TableField(value = "f_material_type")
    private String fMaterialType;

    /**
     * 替代方案
     */
    @ColumnWidth(12) // 设置列宽
    @ExcelProperty("替代方案")
    @JsonProperty("replaceType")
    @TableField(value = "f_replace_type")
    private String fReplaceType;

    /**
     * 变动损耗率%
     */
    @ColumnWidth(15) // 设置列宽
    @ExcelProperty("变动损耗率%")
    @JsonProperty("scrapRate")
    @TableField(value = "f_scrap_rate")
    private String fScrapRate;

    /**
     * 工序
     */
    @ColumnWidth(10) // 设置列宽
    @ExcelProperty("工序")
    @TableField(value = "process")
    private String process;

    /**
     * BOM版本
     */
    @ColumnWidth(15) // 设置列宽
    @ExcelProperty("BOM版本")
    @TableField(value = "bom_version")
    private String bomVersion;

    /**
     * 版本号
     */
    @ExcelIgnore
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}