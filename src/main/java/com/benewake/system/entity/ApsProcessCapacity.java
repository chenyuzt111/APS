package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 工序与产能表
 * @TableName aps_process_capacity
 */
@TableName(value ="aps_process_capacity")
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
    @TableField(value = "process_name")
    private String processName;

    /**
     * 序号
     */
    @TableField(value = "process_number")
    private Integer processNumber;

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
    /**
     * 人数MIN
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}