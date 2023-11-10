package com.benewake.system.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="aps_production_material")
@Data
public class ApsProductionMaterialDto implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    @JsonProperty("moBillNo")
    @TableField(value = "f_mo_bill_no")
    private String fMoBillNo;

    @JsonProperty("materialId2")
    @TableField(value = "f_material_id2")
    private String fMaterialId2;

    @JsonProperty("materialName2")
    @TableField(value = "f_material_name2")
    private String fMaterialName2;

    @JsonProperty("materialType")
    @TableField(value = "f_material_type")
    private String fMaterialType;

    @JsonProperty("mustQty")
    @TableField(value = "f_must_qty")
    private String fMustQty;

    @JsonProperty("pickedQty")
    @TableField(value = "f_picked_qty")
    private String fPickedQty;

    @JsonProperty("goodReturnQty")
    @TableField(value = "f_good_return_qty")
    private String fGoodReturnQty;

    @JsonProperty("processDefectReturnQty")
    @TableField(value = "f_process_defect_return_qty")
    private String fProcessDefectReturnQty;

    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
