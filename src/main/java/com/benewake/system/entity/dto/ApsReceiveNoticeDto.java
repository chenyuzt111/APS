package com.benewake.system.entity.dto;

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
public class ApsReceiveNoticeDto implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    @JsonProperty("mustQty")
    @TableField(value = "f_must_qty")
    private Integer fMustQty;

    @JsonProperty("checkQty")
    @TableField(value = "f_check_qty")
    private Integer fCheckQty;

    @JsonProperty("receiveQty")
    @TableField(value = "f_receive_qty")
    private Integer fReceiveQty;

    @JsonProperty("csnReceiveBaseQty")
    @TableField(value = "f_csn_receive_base_qty")
    private Integer fCsnReceiveBaseQty;

    @JsonProperty("inStockQty")
    @TableField(value = "f_in_stock_qty")
    private Integer fInStockQty;

    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
