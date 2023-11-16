package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @TableName aps_process_scheme
 */

@Data
public class ApsProcessSchemeDto implements Serializable {

    /**
     * ApsProcessCapacityId
     */
    private Integer id;

    /**
     * 当前工艺方案
     */
    @TableField(value = "current_process_scheme")
    private String currentProcessScheme;


    /**
     * 所属工序
     */
    @TableField(value = "belonging_process")
    private String belongingProcess;

    /**
     * 工序id
     */
    @TableField(value = "process_id")
    private Integer processId;
    /**
     * 工序name
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
     * 切换时间（s）
     */
    @TableField(value = "switch_time")
    private BigDecimal switchTime;

    /*
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
     * 员工姓名
     */

    private String employeeName;

    /**
     * 人数
     */

    private Integer number;

    private Boolean state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}