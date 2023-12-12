package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsImmediatelyInventory;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="aps_immediately_inventory")
@Data
public class ApsImmediatelyInventoryMultipleVersions extends ApsImmediatelyInventory implements Serializable {

    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}