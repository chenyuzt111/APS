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

@TableName(value ="aps_purchase_order")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsPurchaseOrderDto implements Serializable {
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

    @JsonProperty("remainReceiveQty")
    @TableField(value = "f_remain_receive_qty")
    private Integer fRemainReceiveQty;

    @JsonProperty("deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    @TableField(value = "f_delivery_date")
    private Date fDeliveryDate;

    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
