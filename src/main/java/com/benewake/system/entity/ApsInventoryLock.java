package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="aps_inventory_lock")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsInventoryLock implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @JsonProperty("expiryDate")
    @TableField(value = "f_expiry_date")
    private Date fExpiryDate;

    @JsonProperty("lockQty")
    @TableField(value = "f_lock_qty")
    private Integer fLockQty;

    @JsonProperty("lot")
    @TableField(value = "f_lot")
    private String fLot;

    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
