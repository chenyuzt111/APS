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

/**
 * 
 * @TableName aps_daily_data_upload
 */
@TableName(value ="aps_daily_data_upload")
@Data
public class ApsDailyDataUploadDto implements Serializable {
    /**
     * 
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @ColumnWidth(12)
    @ExcelProperty("订单编号")
    @TableField(value = "f_order_number")
    @JsonProperty("orderNumber")
    private String orderNumber;

    /**
     * 
     */
    @ColumnWidth(12)
    @ExcelProperty("物料编码")
    @TableField(value = "f_material_code")
    @JsonProperty("materialCode")
    private String materialCode;

    /**
     * 
     */
    @ColumnWidth(22)
    @ExcelProperty("物料名称")
    @TableField(value = "f_material_name")
    @JsonProperty("materialName")
    private String materialName;


    /**
     *
     */
    @ExcelIgnore
    @TableField(value = "process_id")
    @JsonProperty("processId")
    private Integer processId;


    /**
     * 
     */
    @ColumnWidth(20)
    @ExcelProperty("工序名称")
    @TableField(value = "process_name")
    private String processName;


    /**
     * 
     */
    @ColumnWidth(12)
    @ExcelProperty("总数量")
    @TableField(value = "f_total_quantity")
    @JsonProperty("totalQuantity")
    private String totalQuantity;

    /**
     * 
     */
    @ColumnWidth(12)
    @ExcelProperty("完成数量")
    @TableField(value = "f_completed_quantity")
    @JsonProperty("completedQuantity")
    private String completedQuantity;

    /**
     * 
     */
    @ColumnWidth(20)
    @ExcelProperty("产能(秒/台/人)")
    @TableField(value = "f_capacity_ps_pu_pp")
    @JsonProperty("capacityPsPuPp")
    private String capacityPsPuPp;

    /**
     * 
     */
    @ColumnWidth(12)
    @ExcelProperty("剩余数量")
    @TableField(value = "f_remaining_quantity")
    @JsonProperty("remainingQuantity")
    private String remainingQuantity;

    /**
     * 
     */
    @ColumnWidth(12)
    @ExcelProperty("剩余产能")
    @TableField(value = "f_remaining_capacity")
    @JsonProperty("remainingCapacity")
    private String remainingCapacity;

    /**
     * 
     */
    @ExcelIgnore
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}