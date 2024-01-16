package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_common_functions
 */
@TableName(value ="aps_common_functions")
@Data
public class ApsCommonFunctions implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 标题
     */
    private String label;

    /**
     * 界面名
     */
    private String name;

    /**
     * 前端路径
     */
    private String path;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}