package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_daily_data_upload
 */
@TableName(value ="aps_daily_data_upload")
@Data
public class ApsDailyDataUpload implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_order_number")
    private String fOrderNumber;

    /**
     * 
     */
    @TableField(value = "f_material_code")
    private String fMaterialCode;

    /**
     * 
     */
    @TableField(value = "f_process_id")
    private Integer fProcessId;

    /**
     * 
     */
    @TableField(value = "f_total_quantity")
    private String fTotalQuantity;

    /**
     * 
     */
    @TableField(value = "f_completed_quantity")
    private String fCompletedQuantity;

    /**
     * 
     */
    @TableField(value = "f_capacity_ps_pu_pp")
    private String fCapacityPsPuPp;

    /**
     * 
     */
    @TableField(value = "f_remaining_quantity")
    private String fRemainingQuantity;

    /**
     * 
     */
    @TableField(value = "f_remaining_capacity")
    private String fRemainingCapacity;

    /**
     * 
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}