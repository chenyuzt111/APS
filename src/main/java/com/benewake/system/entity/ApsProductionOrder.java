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
 * @TableName aps_production_order
 */
@TableName(value ="aps_production_order")
@Data
public class ApsProductionOrder implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单据编号
     */
    @JsonProperty("billNo")
    @TableField(value = "f_bill_no")
    private String fBillNo;

    /**
     * 单据类型
     */
    @JsonProperty("billType")
    @TableField(value = "f_bill_type")
    private String fBillType;

//    /**
//     * 单据类型ID
//     */
//    @JsonProperty("billTypeId")
//    @TableField(value = "f_bill_type_id")
//    private String fBillTypeId;

    /**
     * 物料编码
     */
    @JsonProperty("materialId")
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 物料名称
     */
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 数量
     */
    @JsonProperty("qty")
    @TableField(value = "f_qty")
    private String fQty;

    /**
     * 业务状态
     */
    @JsonProperty("status")
    @TableField(value = "f_status")
    private String fStatus;

    /**
     * 领料状态
     */
    @JsonProperty("pickMtrlStatus")
    @TableField(value = "f_pick_mtrl_status")
    private String fPickMtrlStatus;

    /**
     * 合格品入库数量
     */
    @JsonProperty("stockInQuaAuxQty")
    @TableField(value = "f_stock_in_qua_aux_qty")
    private String fStockInQuaAuxQty;

    /**
     * BOM版本
     */
    @JsonProperty("bomId")
    @TableField(value = "f_bom_id")
    private String fBomId;

    /**
     * 计划完成时间
     */
    @TableField(value = "planned_completion_time")
    private Date plannedCompletionTime;

/*定制物料编码*/
    @TableField(value = "f_dzmaterial_id")
    private String fDzMaterialName;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}