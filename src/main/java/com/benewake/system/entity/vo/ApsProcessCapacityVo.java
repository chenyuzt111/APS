package com.benewake.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 工序与产能表
 *
 * @TableName aps_process_capacity
 */
@TableName(value = "aps_process_capacity")
@Data
public class ApsProcessCapacityVo implements Serializable {
    /**
     * 唯一标识
     */
    @ExcelIgnore
    private Integer id;

    /**
     * 所属工序
     */
    @ExcelProperty("所属工序")
    private String belongingProcess;

    /**
     * 工序id
     */
    @ExcelIgnore
    private String processId;
    /**
     * 工序id
     */
    @ExcelProperty("工序名称")
    private String processName;

    /**
     * 序号
     */
    @ExcelProperty("序号")
    private Integer processNumber;
    /**
     * 序号
     */
    @ExcelProperty("并行数量")
    private Integer concurrencyCount;

    /**
     * 产品族
     */
    @ExcelProperty("产品族")
    private String productFamily;

    /**
     * 包装方式
     */
    @ExcelProperty("包装方式")
    private String packagingMethod;

    /**
     * 切换时间
     */
    @ExcelProperty("切换时间")
    private Integer switchTime;
    /**
     * 标准工时
     */
    @ExcelProperty("标准工时")
    private String standardTime;

    /**
     * 人数MAX
     */
    @ExcelProperty("人数MAX")
    private Integer maxPersonnel;

    /**
     * 人数MIN
     */
    @ExcelProperty("人数MIN")
    private Integer minPersonnel;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}