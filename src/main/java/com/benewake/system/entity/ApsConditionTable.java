package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_condition_table
 */
@TableName(value ="aps_condition_table")
@Data
public class ApsConditionTable implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "view_col_id")
    private Integer viewColId;

    /**
     * 
     */
    @TableField(value = "value_operator")
    private String valueOperator;

    /**
     * 
     */
    @TableField(value = "col_value")
    private String colValue;

    /**
     * 
     */
    @TableField(value = "col_seq")
    private Integer colSeq;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}