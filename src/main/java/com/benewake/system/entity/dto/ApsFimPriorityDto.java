package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_fim_priority
 */
@TableName(value ="aps_fim_priority")
@Data
public class ApsFimPriorityDto implements Serializable {
    /**
     * 
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @ExcelProperty("生产ID")
    @TableField(value = "f_production_id")
    @JsonProperty("productionId")
    private String fProductionId;

    /**
     * 
     */
    @ExcelProperty("任务来源ID")
    @TableField(value = "f_task_source_id")
    @JsonProperty("taskSourceId")
    private String fTaskSourceId;

    /**
     * 
     */
    @ExcelProperty("物料编码")
    @TableField(value = "f_material_code")
    @JsonProperty("mterialCode")
    private String fMaterialCode;

    /**
     * 
     */
    @ExcelProperty("物料名称")
    @TableField(value = "f_material_name")
    @JsonProperty("materialName")
    private String fMaterialName;

    /**
     * 
     */
    @ExcelProperty("需补货数量")
    @TableField(value = "f_replenishment_quantity")
    @JsonProperty("replenishmentQuantity")
    private String fReplenishmentQuantity;

    /**
     * 
     */
    @ExcelProperty("预计开始时间")
    @TableField(value = "f_expected_start_time")
    @JsonProperty("expectedStartTime")
    private String fExpectedStartTime;

    /**
     * 
     */
    @ExcelProperty("需入库时间")
    @TableField(value = "f_required_delivery_time")
    @JsonProperty("requiredDeliveryTime")
    private String fRequiredDeliveryTime;

    /**
     * 
     */
    @ExcelProperty("优先级")
    @TableField(value = "f_priority")
    @JsonProperty("priority")
    private String fPriority;

    /**
     * 
     */
    @ExcelProperty("所包含销售订单")
    @TableField(value = "f_contained_sales_orders")
    @JsonProperty("containedSalesOrders")
    private String fContainedSalesOrders;



    @ExcelIgnore
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}