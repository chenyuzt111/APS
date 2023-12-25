package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工序与产能表
 * @TableName aps_process_capacity
 */
@TableName(value ="aps_process_capacity")
@Data
public class ApsProcessCapacityDto implements Serializable {
    /**
     * 唯一标识
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属工序
     */
    @ExcelProperty("所属工序")
    @TableField(value = "belonging_process")
    private String belongingProcess;

    /**
     * 工序名称
     */
    @ExcelIgnore
    @TableField(value = "process_id")
    private String processId;
    /**
     * 工序名称
     */
    @ExcelProperty("工序名称")
    @TableField(value = "process_name")
    private String processName;


    /**
     * 产品族
     */
    @ExcelProperty("产品族")
    @TableField(value = "product_family")
    private String productFamily;

    /**
     * 包装方式
     */
    @ExcelProperty("包装方式")
    @TableField(value = "packaging_method")
    private String packagingMethod;
    /**
     * 包装方式
     */
    @ExcelProperty("并行数量")
    @TableField(value = "concurrency_count")
    private Integer concurrencyCount;

    /**
     * 标准工时
     */
    @ExcelProperty("标准工时")
    @TableField(value = "standard_time")
    private BigDecimal standardTime;
    /**
     * 标准工时
     */
    @ExcelProperty("切换时间")
    @TableField(value = "switch_time")
    private Integer switchTime;

    /**
     * 人数MAX
     */
    @ExcelProperty("人数MAX")
    @TableField(value = "max_personnel")
    private Integer maxPersonnel;

    /**
     * 人数MIN
     */
    @ExcelProperty("人数MIN")
    @TableField(value = "min_personnel")
    private Integer minPersonnel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}