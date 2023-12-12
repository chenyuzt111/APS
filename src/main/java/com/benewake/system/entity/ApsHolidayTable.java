package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_holiday_table
 */
@TableName(value ="aps_holiday_table")
@Data
public class ApsHolidayTable implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 
     */
    @TableField(value = "month_day")
    private String monthDay;

    /**
     * 
     */
    @TableField(value = "is_holiday")
    private Boolean isHoliday;

    /**
     * 
     */
    @TableField(value = "name")
    private String name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}