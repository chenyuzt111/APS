package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_process_name_pool
 */
@TableName(value ="aps_process_name_pool")
@Data
public class ApsProcessNamePoolVo implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    private Integer number;

    /**
     * 
     */
    @TableField(value = "process_name")
    private String processName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}