package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName aps_purchase_request
 */
@TableName(value ="aps_purchase_request")
@Data
public class ApsPurchaseRequest implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;
//    /**
//     * 物料编码
//     */
//    @JsonProperty("materialName")
//    @TableField(value = "f_material_name")
//    private String fMaterialName;

    /**
     * 批准数量
     */
    @JsonProperty("baseUnitQty")
    @TableField(value = "f_base_unit_qty")
    private String fBaseUnitQty;

    /**
     * 到货日期
     */
    @JsonProperty("arrivalDate")
    @TableField(value = "f_arrival_date")
    private Date fArrivalDate;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}