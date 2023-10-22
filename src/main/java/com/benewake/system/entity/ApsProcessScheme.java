package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_process_scheme
 */
@TableName(value ="aps_process_scheme")
@Data
public class ApsProcessScheme implements Serializable {
    /**
     * 自增id
     */
    @TableId(value = "id" ,type = IdType.AUTO)
    private Integer id;

    /**
     * 当前工艺方案
     */
    @TableField(value = "current_process_scheme")
    private String currentProcessScheme;

    /**
     * 工艺与产能id
     */
    @TableField(value = "process_capacity_id")
    private Integer processCapacityId;

    /**
     * 员工姓名
     */
    @TableField(value = "employee_name")
    private String employeeName;

    /**
     * 人数
     */
    @TableField(value = "number")
    private Integer number;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}