package com.benewake.system.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="aps_material_bom")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApsMaterialBomDto implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    @JsonProperty("documentStatus")
    @TableField(value = "f_document_status")
    private String fDocumentStatus;

    @JsonProperty("materialIdChild")
    @TableField(value = "f_material_id_child")
    private String fMaterialIdChild;

    @JsonProperty("materialNameChild")
    @TableField(value = "f_material_name_child")
    private String fMaterialNameChild;

    @JsonProperty("numerator")
    @TableField(value = "f_numerator")
    private String fNumerator;

    @JsonProperty("denominator")
    @TableField(value = "f_denominator")
    private String fDenominator;

    @JsonProperty("fixScrapQtyLot")
    @TableField(value = "f_fix_scrap_qty_lot")
    private String fFixScrapQtyLot;

    @JsonProperty("scrapRate")
    @TableField(value = "f_scrap_rate")
    private String fScrapRate;

    @JsonProperty("materialType")
    @TableField(value = "f_material_type")
    private String fMaterialType;

    @JsonProperty("replaceType")
    @TableField(value = "f_replace_type")
    private String fReplaceType;

    @JsonProperty("replaceGroupBop")
    @TableField(value = "f_replace_group_bop")
    private String fReplaceGroupBop;

    @JsonProperty("process")
    @TableField(value = "process")
    private String process;

    @JsonProperty("bomVersion")
    @TableField(value = "bom_version")
    private String bomVersion;

    @JsonProperty("chVersion")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
