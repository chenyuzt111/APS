package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 工序与产能表
 * @TableName aps_process_capacity
 */
@TableName(value ="aps_process_capacity")
@Data
public class ApsProcessCapacityParam implements Serializable {
    /**
     * 唯一标识
     */
    private Integer id;

    /**
     * 所属工序
     */
    private String belongingProcess;

    /**
     * 工序名称
     */
    private String processName;

    /**
     * 序号
     */
    private Integer processNumber;

    /**
     * 产品族
     */
    private String productFamily;


    private Integer concurrencyCount;

    /**
     * 包装方式
     */
    private String packagingMethod;


    /**
     * 切换时间（s）
     */
    private Integer switchTime;

    /**
     * 标准工时
     */
    private String standardTime;

    /**
     * 人数MAX
     */
    private Integer maxPersonnel;

    /**
     * 人数MIN
     */
    private Integer minPersonnel;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}