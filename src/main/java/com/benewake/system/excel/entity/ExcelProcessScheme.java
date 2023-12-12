package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @TableName aps_process_scheme
 */

@Data
public class ExcelProcessScheme implements Serializable {


    /**
     * 当前工艺方案
     */
    @ColumnWidth(22)
    @ExcelProperty("当前工艺方案")
    private String currentProcessScheme;


    /**
     * 所属工序
     */
    @ColumnWidth(12)
    @ExcelProperty("所属工序")
    private String belongingProcess;

    /**
     * 产品族
     */
    @ColumnWidth(11)
    @ExcelProperty("产品族")
    private String productFamily;

    /**
     * 序号
     */
    @ColumnWidth(7)
    @ExcelProperty("序号")
    private Integer processNumber;

    /**
     * 工序name
     */
    @ColumnWidth(26)
    @ExcelProperty("工序名称")
    private String processName;

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
    private BigDecimal standardTime;

    /**
     * 切换时间
     */
    @ColumnWidth(12)
    @ExcelProperty("切换时间")
    private BigDecimal switchTime;

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

    /**
     * 员工姓名
     */
    @ColumnWidth(12)
    @ExcelProperty("员工姓名")
    private String employeeName;

    /**
     * 人数
     */
    @ColumnWidth(7)
    @ExcelProperty("人数")
    private Integer number;


    @ColumnWidth(7)
    @ExcelProperty("状态")
    private String state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}