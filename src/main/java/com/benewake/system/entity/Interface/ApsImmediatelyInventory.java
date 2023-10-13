package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="aps_immediately_inventory")
@Data
public class ApsImmediatelyInventory implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 仓库名称
     */
    @TableField(value = "f_stock_name")
    private String fStockName;

    /**
     * 库存量(基本单位)
     */
    @TableField(value = "f_base_qty")
    private Integer fBaseQty;

    /**
     * 可用量(主单位)
     */
    @TableField(value = "f_avb_qty")
    private Integer fAvbQty;

    /**
     * 批号
     */
    @TableField(value = "f_lot")
    private String fLot;

    /**
     * 有效期至
     */
    @TableField(value = "f_expiry_date")
    private String fExpiryDate;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}