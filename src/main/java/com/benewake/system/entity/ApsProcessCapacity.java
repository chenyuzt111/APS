package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工序与产能表
 *
 * @TableName aps_process_capacity
 */
@TableName(value = "aps_process_capacity")
@Data
public class ApsProcessCapacity implements Serializable {
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属工序
     */
    @TableField(value = "belonging_process")
    private String belongingProcess;

    /**
     * 工序名称
     */
    @TableField(value = "process_id")
    private Integer processId;
//    /**
//     * 工序名称
//     */
//    @TableField(value = "process_name")
//    private String processName;


    /**
     * 序号
     */
    @TableField(value = "process_number")
    private Integer processNumber;

    /**
     * 序号
     */
    @TableField(value = "concurrency_count")
    private Integer concurrencyCount;

    /**
     * 产品族
     */
    @TableField(value = "product_family")
    private String productFamily;

    /**
     * 包装方式
     */
    @TableField(value = "packaging_method")
    private String packagingMethod;

    /**
     * 切换时间 （s）
     */
    @TableField(value = "switch_time")
    private Integer switchTime;

    /**
     * 标准工时
     */
    @TableField(value = "standard_time")
    private BigDecimal standardTime;

    /**
     * 人数MAX
     */
    @TableField(value = "max_personnel")
    private Integer maxPersonnel;

    /**
     * 人数MIN
     */
    @TableField(value = "min_personnel")
    private Integer minPersonnel;
//    /**
//     * 人数MIN
//     */
//    @TableField(value = "version")
//    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}