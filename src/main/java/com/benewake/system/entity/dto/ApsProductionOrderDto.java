package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName aps_production_order
 */
@TableName(value ="aps_production_order")
@Data
public class ApsProductionOrderDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单据编号
     */
    @ColumnWidth(20) // 调整列宽为20
    @ExcelProperty("单据编号")
    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    /**
     * 单据类型
     */
    @ColumnWidth(15) // 调整列宽为15
    @ExcelProperty("单据类型")
    @JsonProperty("billType")
    @TableField(value = "f_bill_type")
    private String fBillType;

    /**
     * 单据类型ID
     */
    @ColumnWidth(15) // 调整列宽为15
    @ExcelProperty("单据类型ID")
    @JsonProperty("billTypeId")
    @TableField(value = "f_bill_type_id")
    private String fBillTypeId;

    /**
     * 物料编码
     */
    @ColumnWidth(20) // 调整列宽为20
    @ExcelProperty("物料编码")
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 物料名称
     */
    @ColumnWidth(30) // 调整列宽为30
    @ExcelProperty("物料名称")
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 数量
     */
    @ColumnWidth(10) // 调整列宽为10
    @ExcelProperty("数量")
    @JsonProperty("qty")
    @TableField(value = "f_qty")
    private Integer fQty;

    /**
     * 业务状态
     */
    @ColumnWidth(15) // 调整列宽为15
    @ExcelProperty("业务状态")
    @JsonProperty("status")
    @TableField(value = "f_status")
    private String fStatus;

    /**
     * 领料状态
     */
    @ColumnWidth(15) // 调整列宽为15
    @ExcelProperty("领料状态")
    @JsonProperty("pickMtrlStatus")
    @TableField(value = "f_pick_mtrl_status")
    private String fPickMtrlStatus;

    /**
     * 合格品入库数量
     */
    @ColumnWidth(15) // 调整列宽为15
    @ExcelProperty("合格品入库数量")
    @JsonProperty("stockInQuaAuxQty")
    @TableField(value = "f_stock_in_qua_aux_qty")
    private Integer fStockInQuaAuxQty;

    /**
     * BOM版本
     */
    @ColumnWidth(20) // 调整列宽为20
    @ExcelProperty("BOM版本")
    @JsonProperty("bomId")
    @TableField(value = "f_bom_id")
    private String fBomId;

    /**
     * 完成时间
     */
    @ColumnWidth(20) // 调整列宽为20
    @ExcelProperty("完成时间")
    @TableField(value = "planned_completion_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date plannedCompletionTime;

    /**
     * 版本号
     */
    @ColumnWidth(15) // 调整列宽为15
    @ExcelProperty("版本号")
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
