package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName aps_attendance
 */
@TableName(value ="aps_attendance")
@Data
public class ApsAttendanceParam implements Serializable {
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "date")
    private Date date;


    /**
     * 出勤时间范围
     */
    @TableField(value = "attendance_time_range")
    private String attendanceTimeRange;

    /**
     * 午休时间范围
     */
    @TableField(value = "lunch_break_time_range")
    private String lunchBreakTimeRange;

    /**
     * 晚饭时间范围
     */
    @TableField(value = "dinner_time_range")
    private String dinnerTimeRange;

    /**
     * 早会时间范围
     */
    @TableField(value = "morning_meeting_time_range")
    private String morningMeetingTimeRange;

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