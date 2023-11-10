package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_production_plan
 */
@TableName(value ="aps_production_plan")
@Data
public class ApsProductionPlan implements Serializable {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    
    @TableField(value = "f_task_id")
    private String fTaskId;

    
    @TableField(value = "f_task_source_id")
    private String fTaskSourceId;

    
    @TableField(value = "f_material_code")
    private String fMaterialCode;


    @TableField(value = "f_total_quantity")
    private String fTotalQuantity;

    
    @TableField(value = "f_completed_quantity")
    private String fCompletedQuantity;

    
    @TableField(value = "f_actual_start_time")
    private String fActualStartTime;

    
    @TableField(value = "f_actual_completion_time")
    private String fActualCompletionTime;

    
    @TableField(value = "f_required_delivery_time")
    private String fRequiredDeliveryTime;

    
    @TableField(value = "f_on_time_completion")
    private String fOnTimeCompletion;

    
    @TableField(value = "f_delay_days")
    private String fDelayDays;

    
    @TableField(value = "f_priority")
    private String fPriority;

    
    @TableField(value = "f_unfinished_reason")
    private String fUnfinishedReason;

    
    @TableField(value = "f_related_orders")
    private String fRelatedOrders;

    
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}