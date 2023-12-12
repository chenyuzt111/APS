package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_time_sheet
 */
@TableName(value ="aps_time_sheet")
@Data
public class ApsTimeSheet implements Serializable {
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}