package com.benewake.system.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @TableName aps_immediately_inventory
 */
@TableName(value ="aps_immediately_inventory")
@Data
public class ApsImmediatelyInventoryDto implements Serializable {
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
     * 仓库名称
     */
    @JsonProperty("stockName") // 使用@JsonProperty注解修改属性名
    @TableField(value = "f_stock_name")
    private String fStockName;

    /**
     * 库存量(基本单位)
     */
    @JsonProperty("baseQty") // 使用@JsonProperty注解修改属性名
    @TableField(value = "f_base_qty")
    private Integer fBaseQty;

    /**
     * 可用量(主单位)
     */
    @JsonProperty("avbQty") // 使用@JsonProperty注解修改属性名
    @TableField(value = "f_avb_qty")
    private Integer fAvbQty;

    /**
     * 批号
     */
    @JsonProperty("lot") // 使用@JsonProperty注解修改属性名
    @TableField(value = "f_lot")
    private String fLot;

    /**
     * 有效期至
     */
    @JsonProperty("expiryDate") // 使用@JsonProperty注解修改属性名
    @TableField(value = "f_expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date fExpiryDate;

    /**
     * 版本号
     */
    @JsonProperty("chVersion") // 使用@JsonProperty注解修改属性名
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
