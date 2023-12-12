package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;


@Data
public class ExcelProcessNamePool implements Serializable {

    @ExcelProperty("序号")
    private Integer number;


    @ColumnWidth(20)
    @ExcelProperty("工序名称")
    private String processName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}