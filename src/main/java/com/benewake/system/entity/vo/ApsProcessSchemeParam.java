package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @TableName aps_process_scheme
 */

@Data
public class ApsProcessSchemeParam implements Serializable {

    /**12
     * ApsProcessCapacityId
     */
    private Integer id;

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
     * 员工姓名
     */

    private String employeeName;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}