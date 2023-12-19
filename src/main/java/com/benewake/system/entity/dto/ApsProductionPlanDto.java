package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName aps_production_plan
 */
@TableName(value = "aps_production_plan")
@Data
public class ApsProductionPlanDto implements Serializable {

    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ExcelProperty("任务号")
    private String ftaskId;

    @ExcelProperty("任务来源")
    private String ftaskSourceId;

    @ExcelProperty("物料编号")
    private String fmaterialCode;

    @ExcelProperty("物料名称")
    private String fmaterialName;

    @ExcelProperty("总数量")
    private String ftotalQuantity;

    @ExcelProperty("完成数量")
    private String fcompletedQuantity;

    @ExcelProperty("实际开始时间")
    private Date factualStartTime;

    @ExcelProperty("实际完成时间")
    private Date factualCompletionTime;

    @ExcelProperty("需入库时间")
    private String frequiredDeliveryTime;

    @ExcelProperty("是否按时完成")
    private String fonTimeCompletion;

    @ExcelProperty("延期天数")
    private String fdelayDays;

    @ExcelProperty("优先级")
    private String fpriority;

    @ExcelProperty("未完成原因")
    private String funfinishedReason;

    @ExcelProperty("所包含订单")
    private String frelatedOrders;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}