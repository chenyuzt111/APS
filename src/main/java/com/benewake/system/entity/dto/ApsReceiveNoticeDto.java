package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="aps_receive_notice")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsReceiveNoticeDto implements Serializable {
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ColumnWidth(20) // 单据编号
    @ExcelProperty("单据编号")
    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    @ColumnWidth(20) // 物料编码
    @ExcelProperty("物料编码")
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @ColumnWidth(30) // 物料名称
    @ExcelProperty("物料名称")
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    @ColumnWidth(15) // 实收数量
    @ExcelProperty("实收数量")
    @JsonProperty("mustQty")
    @TableField(value = "f_must_qty")
    private Integer fMustQty;

    @ColumnWidth(15) // 检测数量
    @ExcelProperty("检测数量")
    @JsonProperty("checkQty")
    @TableField(value = "f_check_qty")
    private Integer fCheckQty;

    @ColumnWidth(15) // 合格数量
    @ExcelProperty("合格数量")
    @JsonProperty("receiveQty")
    @TableField(value = "f_receive_qty")
    private Integer fReceiveQty;

    @ColumnWidth(20) // 让步接收数量(基本数量)
    @ExcelProperty("让步接收数量(基本数量)")
    @JsonProperty("csnReceiveBaseQty")
    @TableField(value = "f_csn_receive_base_qty")
    private Integer fCsnReceiveBaseQty;

    @ColumnWidth(15) // 入库数量
    @ExcelProperty("入库数量")
    @JsonProperty("inStockQty")
    @TableField(value = "f_in_stock_qty")
    private Integer fInStockQty;

    @ColumnWidth(15) // 版本号
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
