package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 
 * @TableName aps_search_history
 */
@TableName(value ="aps_search_history")
@Data
public class ApsSearchHistory implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 
     */
    @TableField(value = "col_id")
    private Integer colId;

    /**
     *
     */
    @TableField(value = "search_query")
    private String searchQuery;



    @TableField(value = "search_time")
    private Timestamp searchTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}