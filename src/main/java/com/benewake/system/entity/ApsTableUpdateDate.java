package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName aps_table_update_date
 */
@TableName(value ="aps_table_update_date")
@Data
public class ApsTableUpdateDate implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "table_id")
    private Integer tableId;

    /**
     * 
     */
    @TableField(value = "update_date")
    private Date updateDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}