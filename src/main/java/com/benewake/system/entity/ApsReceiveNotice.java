package com.benewake.system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="aps_receive_notice")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsReceiveNotice implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

//    @JsonProperty("materialName")
//    @TableField(value = "f_material_name")
//    private String fMaterialName;

    @JsonProperty("mustQty")
    @TableField(value = "f_must_qty")
    private String fMustQty;

    @JsonProperty("checkQty")
    @TableField(value = "f_check_qty")
    private String fCheckQty;

    @JsonProperty("receiveQty")
    @TableField(value = "f_receive_qty")
    private String fReceiveQty;

    @JsonProperty("csnReceiveBaseQty")
    @TableField(value = "f_csn_receive_base_qty")
    private String fCsnReceiveBaseQty;

    @JsonProperty("inStockQty")
    @TableField(value = "f_in_stock_qty")
    private String fInStockQty;

    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
