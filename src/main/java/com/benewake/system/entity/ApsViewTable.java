package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

/**
 * 
 * @TableName aps_view_table
 */
@TableName(value ="aps_view_table")
@Data
@ToString
public class ApsViewTable implements Serializable {
    /**
     * 
     */
    @TableId(value = "view_id", type = IdType.AUTO)
    private Integer viewId;

    /**
     * 
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 
     */
    @TableField(value = "view_name")
    private String viewName;

    /**
     * 
     */
    @TableField(value = "table_id")
    private Integer tableId;

    /**
     *
     */
    @TableField(value = "is_default")
    private Boolean isDefault;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}