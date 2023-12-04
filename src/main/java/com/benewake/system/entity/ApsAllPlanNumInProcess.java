package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_all_plan_num_in_process
 */
@TableName(value ="aps_all_plan_num_in_process")
@Data
public class ApsAllPlanNumInProcess implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务号
     */
    @TableField(value = "task_id")
    private Integer taskId;

    /**
     * 任务来源ID
     */
    @TableField(value = "source_id")
    private String sourceId;

    /**
     * 物料编码
     */
    @TableField(value = "material_code")
    private String materialCode;

    /**
     * 物料名称
     */
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 对应工序
     */
    @TableField(value = "process_name")
    private String processName;

    /**
     * 所做数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 日期
     */
    @TableField(value = "task_date")
    private String taskDate;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    @JsonFormat(pattern = "HH:mm")
    private String startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private String endTime;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}