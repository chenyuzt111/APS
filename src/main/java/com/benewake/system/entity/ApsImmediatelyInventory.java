package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName immediately_inventory
 */
@TableName(value ="aps_immediately_inventory")
@Data
public class ApsImmediatelyInventory implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String fMaterialId;

    /**
     * 
     */
    private String fStockName;

    /**
     * 
     */
    private String fAvbQty;

    /**
     * 
     */
    private String fLot;

    /**
     * 
     */
    private String fExpiryDate;

    /**
     *
     */
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}