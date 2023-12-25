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
    private String productionId;

    /**
     * 
     */
    @ExcelProperty("任务来源ID")
    private String taskSourceId;

    /**
     * 
     */
    @ExcelProperty("物料编码")
    private String mterialCode;

    /**
     * 
     */
    @ExcelProperty("物料名称")
    private String materialName;

    /**
     * 
     */
    @ExcelProperty("需补货数量")
    private String replenishmentQuantity;

    /**
     * 
     */
    @ExcelProperty("预计开始时间")
    private String expectedStartTime;

    /**
     * 
     */
    @ExcelProperty("需入库时间")
    private String requiredDeliveryTime;

    /**
     * 
     */
    @ExcelProperty("优先级")
    private String priority;

    /**
     * 
     */
    @ExcelProperty("所包含销售订单")
    private String containedSalesOrders;


    @ExcelProperty("版本号")
    private String chVersion;

    @ExcelIgnore
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}