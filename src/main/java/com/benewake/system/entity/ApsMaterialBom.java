package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_material_bom
 */
@TableName(value ="aps_material_bom")
@Data
public class ApsMaterialBom implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @TableField(value = "f_number")
    private String fNumber;

    /**
     * 使用组织ID
     */
    @TableField(value = "f_use_org_id")
    private String fUseOrgId;

    /**
     * 父项物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 数据状态
     */
    @TableField(value = "f_document_status")
    private String fDocumentStatus;

    /**
     * 子项物料编码
     */
    @TableField(value = "f_material_id_child")
    private String fMaterialIdChild;

    /**
     * 用量:分子
     */
    @TableField(value = "f_numerator")
    private String fNumerator;

    /**
     * 用量:分母
     */
    @TableField(value = "f_denominator")
    private String fDenominator;

    /**
     * 固定损耗
     */
    @TableField(value = "f_fix_scrap_qty_lot")
    private String fFixScrapQtyLot;

    /**
     * 变动损耗率%
     */
    @TableField(value = "f_scrap_rate")
    private String fScrapRate;

    /**
     *   //子项类型
     */
    @TableField(value = "f_material_type")
    private String fMaterialType;

    /**
     * 替代方案
     */
    @TableField(value = "f_replace_type")
    private String fReplaceType;

    /**
     * 禁用状态
     */
    @TableField(value = "f_forbid_status")
    private String fForbidStatus;

    /**
     * 到期日期
     */
    @TableField(value = "f_expire_date")
    private String fExpireDate;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}