package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName aps_product_family_machine_table
 */
@TableName(value = "aps_product_family_machine_table")
@Data
public class ExcelMachineTableTemplate implements Serializable {



    /**
     * 机器名称
     */
    @ColumnWidth(14)
    @ExcelProperty("机器名称")
    @TableField(value = "f_machine_name")
    @JsonProperty("fMachineName")
    private String fMachineName;

    /**
     * 产品族
     */
    @ColumnWidth(12)
    @ExcelProperty("产品族")
    @TableField(value = "f_product_family")
    @JsonProperty("fProductFamily")
    private String fProductFamily;


    @ColumnWidth(18)
    @ExcelProperty("工序名称")
    @JsonProperty("fProcess")
    @TableField(value = "process_name")
    private String processName;


    /**
     * 机器规格
     */
    @ColumnWidth(40)
    @ExcelProperty("机器规格")
    @TableField(value = "f_machine_configuration")
    @JsonProperty("fMachineConfiguration")
    private String fMachineConfiguration;

    /**
     * 使用车间
     */
    @ColumnWidth(12)
    @ExcelProperty("使用车间")
    @TableField(value = "f_workshop")
    @JsonProperty("fWorkshop")
    private String fWorkshop;


    /**
     * 不可用日期
     */
    @ColumnWidth(40)
    @ExcelProperty("不可用日期")
    @TableField(value = "unavailable_dates")
    private String unavailableDates;

    @ExcelIgnore
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}