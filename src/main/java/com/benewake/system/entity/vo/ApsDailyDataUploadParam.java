package com.benewake.system.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_daily_data_upload
 */
@TableName(value ="aps_daily_data_upload")
@Data
public class ApsDailyDataUploadParam implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private String orderNumber;

    /**
     * 
     */
    private String materialCode;

    /**
     * 
     */
    private Integer processId;

    /**
     * 
     */
    private String totalQuantity;

    /**
     * 
     */
    private String completedQuantity;

    /**
     * 
     */
    private String capacityPsPuPp;

    /**
     * 
     */
    private String remainingQuantity;

    /**
     * 
     */
    private String remainingCapacity;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}