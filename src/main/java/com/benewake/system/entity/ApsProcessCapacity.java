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
    @TableField(value = "belonging_process", updateStrategy = FieldStrategy.IGNORED)
    private String belongingProcess;

    /**
     * 工序名称
     */
    @TableField(value = "process_id", updateStrategy = FieldStrategy.IGNORED)
    private Integer processId;
//    /**
//     * 工序名称
//     */
//    @TableField(value = "process_name")
//    private String processName;


    /**
     * 序号
     */
    @TableField(value = "process_number", updateStrategy = FieldStrategy.IGNORED)
    private Integer processNumber;

    /**
     * 序号
     */
    @TableField(value = "concurrency_count", updateStrategy = FieldStrategy.IGNORED)
    private Integer concurrencyCount;

    /**
     * 产品族
     */
    @TableField(value = "product_family", updateStrategy = FieldStrategy.IGNORED)
    private String productFamily;

    /**
     * 包装方式
     */
    @TableField(value = "packaging_method", updateStrategy = FieldStrategy.IGNORED)
    private String packagingMethod;

    /**
     * 切换时间 （s）
     */
    @TableField(value = "switch_time", updateStrategy = FieldStrategy.IGNORED)
    private Integer switchTime;

    /**
     * 标准工时
     */
    @TableField(value = "standard_time", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal standardTime;

    /**
     * 人数MAX
     */
    @TableField(value = "max_personnel", updateStrategy = FieldStrategy.IGNORED)
    private Integer maxPersonnel;

    /**
     * 人数MIN
     */
    @TableField(value = "min_personnel", updateStrategy = FieldStrategy.IGNORED)
    private Integer minPersonnel;
//    /**
//     * 人数MIN
//     */
//    @TableField(value = "version", updateStrategy = FieldStrategy.IGNORED)
//    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}