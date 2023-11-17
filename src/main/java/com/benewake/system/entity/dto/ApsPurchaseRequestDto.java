package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 
 * @TableName aps_purchase_request
 */
@TableName(value ="aps_purchase_request")
@Data
public class ApsPurchaseRequestDto implements Serializable {
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
    /**
     * 物料编码
     */
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date fArrivalDate;

    /**
     * 版本号
     */
    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}