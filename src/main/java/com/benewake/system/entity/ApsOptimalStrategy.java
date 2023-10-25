package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_optimal_strategy
 */
@TableName(value ="aps_optimal_strategy")
@Data
public class ApsOptimalStrategy implements Serializable {
    /**
     * 自增id
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 产品族
     */
    @TableField(value = "product_family")
    private String productFamily;

    /**
     * 人数
     */
    @TableField(value = "number")
    private Integer number;

    /**
     * 策略 1- 时间 2-平衡率优先
     */
    @TableField(value = "strategy")
    private Integer strategy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}