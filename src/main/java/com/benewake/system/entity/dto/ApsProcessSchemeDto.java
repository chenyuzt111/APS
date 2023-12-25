package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @TableName aps_process_scheme
 */

@Data
public class ApsProcessSchemeDto implements Serializable {

    /**
     * ApsProcessCapacityId
     */
    @ExcelIgnore
    private Integer id;

    /**
     * 当前工艺方案
     */
    @ExcelProperty("当前工艺方案")
    private String currentProcessScheme;


    /**
     * 所属工序
     */
    @ExcelProperty("所属工序")
    private String belongingProcess;

    /**
     * 工序id
     */
    @ExcelIgnore
    private Integer processId;
    /**
     * 工序name
     */
    @ExcelProperty("工序名称")
    private String processName;

    /**
     * 序号
     */
    @ExcelProperty("序号")
    private Integer processNumber;

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
     * 切换时间（s）
     */
    @ExcelProperty("切换时间（s）")
    private BigDecimal switchTime;

    /*
     * 标准工时
     */
    @ExcelProperty("标准工时")
    private BigDecimal standardTime;

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

    /**
     * 员工姓名
     */
    @ExcelProperty("员工姓名")
    private String employeeName;

    /**
     * 人数
     */
    @ExcelProperty("人数")
    private Integer number;

    @ExcelProperty("状态")
    private String state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}