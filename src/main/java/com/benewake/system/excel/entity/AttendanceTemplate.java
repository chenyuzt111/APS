package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AttendanceTemplate implements Serializable {
    /**
     * 员工姓名
     */
    @ExcelProperty("员工姓名")
    private String employeeName;

    /**
     * 日期
     */
    @ExcelProperty("日期")
    private Date date;


    /**
     * 请假时间范围
     */
    @ExcelProperty("请假时间范围")
    private String leaveTimeRange;

}
