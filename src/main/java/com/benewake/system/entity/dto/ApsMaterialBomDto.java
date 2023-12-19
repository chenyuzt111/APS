package com.benewake.system.entity.dto;

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
public class ApsMaterialBomDto implements Serializable {
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ExcelProperty("父项物料编码")
    @ColumnWidth(20) // 设置列宽为20
    private String materialId;

    @ExcelProperty("父项物料名称")
    @ColumnWidth(30) // 设置列宽为30
    private String materialName;

    @ExcelProperty("数据状态")
    @ColumnWidth(15) // 设置列宽为15
    private String documentStatus;

    @ExcelProperty("子项物料编码")
    @ColumnWidth(20) // 设置列宽为20
    private String materialIdChild;

    @ExcelProperty("子项物料名称")
    @ColumnWidth(30) // 设置列宽为30
    private String materialNameChild;

    @ExcelProperty("用量：分子")
    @ColumnWidth(15) // 设置列宽为15
    private Integer numerator;

    @ExcelProperty("用量：分母")
    @ColumnWidth(15) // 设置列宽为15
    private Integer denominator;

    @ExcelProperty("固定损耗")
    @ColumnWidth(15) // 设置列宽为15
    private Integer fixScrapQtyLot;

    @ExcelProperty("变动损耗率%")
    @ColumnWidth(15) // 设置列宽为15
    private Integer scrapRate;

    @ExcelProperty("子项类型")
    @ColumnWidth(15) // 设置列宽为15
    private String materialType;

    @ExcelProperty("替代方案")
    @ColumnWidth(15) // 设置列宽
    private String replaceType;

    @ExcelProperty("项次")
    @ColumnWidth(20) // 设置列宽为20
    private String replaceGroupBop;

    @ExcelProperty("工序")
    @TableField(value = "process")
    @ColumnWidth(15) // 设置列宽为15
    private String process;

    @ExcelProperty("BOM版本")
    @TableField(value = "bom_version")
    @ColumnWidth(15) // 设置列宽为15
    private String bomVersion;

    @ExcelProperty("版本号")
    @ColumnWidth(15) // 设置列宽为15
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
