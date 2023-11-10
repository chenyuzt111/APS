package com.benewake.system.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="aps_outsourced_order")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsOutsourcedOrder implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    @JsonProperty("billType")
    @TableField(value = "f_bill_type")
    private String fBillType;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

//    @JsonProperty("materialName")
//    @TableField(value = "f_material_name")
//    private String fMaterialName;

    @JsonProperty("qty")
    @TableField(value = "f_qty")
    private String fQty;

    @JsonProperty("status")
    @TableField(value = "f_status")
    private String fStatus;

    @JsonProperty("pickMtrlStatus")
    @TableField(value = "f_pick_mtrl_status")
    private String fPickMtrlStatus;

    @JsonProperty("stockInQty")
    @TableField(value = "f_stock_in_qty")
    private String fStockInQty;

    @JsonProperty("bomId")
    @TableField(value = "f_bom_id")
    private String fBomId;

    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
