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

@TableName(value ="aps_outsourced_order")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsOutsourcedOrderDto implements Serializable {
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ColumnWidth(20) // 单据编码
    @ExcelProperty("单据编码")
    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    @ColumnWidth(15) // 单据类型
    @ExcelProperty("单据类型")
    @JsonProperty("billType")
    @TableField(value = "f_bill_type")
    private String fBillType;

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

    @ColumnWidth(10) // 数量
    @ExcelProperty("批号")
    @JsonProperty("lot")
    @TableField(value = "f_lot")
    private String fLot;

    @ColumnWidth(10) // 数量
    @ExcelProperty("数量")
    @JsonProperty("qty")
    @TableField(value = "f_qty")
    private Integer fQty;

    @ColumnWidth(15) // 业务状态
    @ExcelProperty("业务状态")
    @JsonProperty("status")
    @TableField(value = "f_status")
    private String fStatus;

    @ColumnWidth(15) // 领料状态
    @ExcelProperty("领料状态")
    @JsonProperty("pickMtrlStatus")
    @TableField(value = "f_pick_mtrl_status")
    private String fPickMtrlStatus;

    @ColumnWidth(10) // 入库数量
    @ExcelProperty("入库数量")
    @JsonProperty("stockInQty")
    @TableField(value = "f_stock_in_qty")
    private Integer fStockInQty;

    @ColumnWidth(20) // BOM版本
    @ExcelProperty("BOM版本")
    @JsonProperty("bomId")
    @TableField(value = "f_bom_id")
    private String fBomId;

    @ColumnWidth(15) // 版本号
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
