package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @TableName aps_inventory_lock
 */
@TableName(value ="aps_inventory_lock")
@Data
public class ApsInventoryLock implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 字段物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 到期日
     */
    @TableField(value = "f_expiry_date")
    private String fExpiryDate;

    /**
     * 锁库数量
     */
    @TableField(value = "f_lock_qty")
    private String fLockQty;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}