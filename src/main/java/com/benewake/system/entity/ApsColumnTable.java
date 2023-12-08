package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_column_table
 */
@TableName(value ="aps_column_table")
@Data
public class ApsColumnTable implements Serializable {
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
    @TableField(value = "ch_col_name")
    private String chColName;

    /**
     * 
     */
    @TableField(value = "en_col_name")
    private String enColName;


    /**
     *
     */
    @TableField(value = "vo_col_name")
    private String voColName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}