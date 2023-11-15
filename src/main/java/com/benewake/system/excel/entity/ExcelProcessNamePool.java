package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@Data
public class ExcelProcessNamePool implements Serializable {

    @ExcelProperty("序号")
    private Integer number;


    @ExcelProperty("工序名称")
    private String processName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}