package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsInventoryLock;
import lombok.Data;

import java.io.Serializable;

/**
 * 用于存储库存锁定信息的表
 * @TableName aps_inventory_lock
 */
@TableName(value ="aps_inventory_lock")
@Data
public class ApsInventoryLockMultipleVersions extends ApsInventoryLock implements Serializable {

    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}