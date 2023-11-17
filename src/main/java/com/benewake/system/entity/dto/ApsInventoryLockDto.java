package com.benewake.system.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@TableName(value ="aps_inventory_lock")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsInventoryLockDto implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String FMaterialName;

    @JsonProperty("expiryDate")
    @TableField(value = "f_expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date fExpiryDate;

    @JsonProperty("lockQty")
    @TableField(value = "f_lock_qty")
    private Integer fLockQty;

    @JsonProperty("lot")
    @TableField(value = "f_lot")
    private String fLot;

    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
