package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
public class ExcelProcessCapacity implements Serializable {


    /**
     * 所属工序
     */
    @ColumnWidth(12)
    @ExcelProperty("所属工序")
    private String belongingProcess;

    /**
     * 产品族
     */
    @ColumnWidth(12)
    @ExcelProperty("产品族")
    private String productFamily;

    /**
     * 序号
     */
    @ColumnWidth(8)
    @ExcelProperty("序号")
    private Integer processNumber;


    /**
     * 工序id
     */
    @ColumnWidth(19)
    @ExcelProperty("工序名称")
    private String processName;

    /**
     * 标准工时
     */
    @ColumnWidth(12)
    @ExcelProperty("切换时间")
    private Integer switchTime;



    /**
     * 包装方式
     */
    @ColumnWidth(12)
    @ExcelProperty("包装方式")
    private String packagingMethod;

    /**
     * 标准工时
     */
    @ColumnWidth(12)
    @ExcelProperty("标准工时")
    private String standardTime;

    /**
     * 人数MAX
     */
    @ColumnWidth(12)
    @ExcelProperty("人数MAX")
    private Integer maxPersonnel;

    /**
     * 人数MIN
     */
    @ColumnWidth(12)
    @ExcelProperty("人数MIN")
    private Integer minPersonnel;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}