package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_semi_finished_basic_data
 */
@TableName(value ="aps_semi_finished_basic_data")
@Data
public class ApsSemiFinishedBasicDataVo implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String materialCode;

    /**
     *
     */
    private String materialName;

    /**
     * 
     */
    private String materialProperty;

    /**
     * 
     */
    private String materialGroup;

    /**
     * 
     */
    private String productType;

    /**
     * 
     */
    private String procurementLeadTime;

    /**
     * 
     */
    @TableField(value = "f_moq")
    private String moq;

    /**
     * 
     */
    @TableField(value = "f_mpq")
    private String mpq;

    /**
     * 
     */
    @TableField(value = "f_safety_stock")
    private String safetyStock;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}