package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@TableName(value ="aps_material_bom")
@Data
public class ApsMaterialBomDto implements Serializable {
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ExcelProperty("父项物料编码")
    @TableField(value = "f_material_id")
    @JsonProperty("materialId")
    @ColumnWidth(20) // 设置列宽为20
    private String fMaterialId;

    @ExcelProperty("父项物料名称")
    @TableField(value = "f_material_name")
    @ColumnWidth(30) // 设置列宽为30
    @JsonProperty("materialName")
    private String fMaterialName;

    @ExcelProperty("数据状态")
    @TableField(value = "f_document_status")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("documentStatus")
    private String fDocumentStatus;

    @ExcelProperty("子项物料编码")
    @TableField(value = "f_material_id_child")
    @ColumnWidth(20) // 设置列宽为20
    @JsonProperty("materialIdChild")
    private String fMaterialIdChild;

    @ExcelProperty("子项物料名称")
    @TableField(value = "f_material_name_child")
    @ColumnWidth(30) // 设置列宽为30
    @JsonProperty("materialNameChild")
    private String fMaterialNameChild;

    @ExcelProperty("用量：分子")
    @TableField(value = "f_numerator")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("numerator")
    private String fNumerator;

    @ExcelProperty("用量：分母")
    @TableField(value = "f_denominator")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("denominator")
    private String fDenominator;

    @ExcelProperty("变动损耗率%")
    @TableField(value = "f_fix_scrap_qty_lot")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("fixScrapQtyLot")
    private String fFixScrapQtyLot;

    @ExcelProperty("子项类型")
    @TableField(value = "f_scrap_rate")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("scrapRate")
    private String fScrapRate;

    @ExcelProperty("替代方案")
    @TableField(value = "f_material_type")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("materialType")
    private String fMaterialType;

    @ExcelProperty("项次")
    @TableField(value = "f_replace_type")
    @ColumnWidth(15) // 设置列宽
    @JsonProperty("replaceType")// 为15
    private String fReplaceType;

    @ExcelProperty("父项物料编码")
    @TableField(value = "f_replace_group_bop")
    @ColumnWidth(20) // 设置列宽为20
    @JsonProperty("replaceGroupBop")
    private String fReplaceGroupBop;

    @ExcelProperty("工序")
    @TableField(value = "process")
    @ColumnWidth(15) // 设置列宽为15
    private String process;

    @ExcelProperty("BOM版本")
    @TableField(value = "bom_version")
    @ColumnWidth(15) // 设置列宽为15
    private String bomVersion;

    @ExcelProperty("版本号")
    @TableField(value = "version")
    @ColumnWidth(15) // 设置列宽为15
    @JsonProperty("chVersion")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
