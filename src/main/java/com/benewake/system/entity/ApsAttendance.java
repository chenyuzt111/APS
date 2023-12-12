package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName aps_attendance
 */
@TableName(value ="aps_attendance")
@Data
public class ApsAttendance implements Serializable {
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 员工姓名
     */
    @TableField(value = "employee_name")
    private String employeeName;

    /**
     * 日期
     */
    @TableField(value = "date")
    private Date date;

    /**
     * 星期
     */
    @TableField(value = "day_of_week")
    private String dayOfWeek;

    /**
     * 是否为工作日
     */
    @TableField(value = "is_workday")
    private Boolean isWorkday;

    /**
     * 请假时间范围
     */
    @TableField(value = "leave_time_range")
    private String leaveTimeRange;

    /**
     * 有效出勤时间范围
     */
    @TableField(value = "effective_attendance_time_range")
    private String effectiveAttendanceTimeRange;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}