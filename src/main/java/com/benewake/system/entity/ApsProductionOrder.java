package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

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
    @TableField(value = "f_bill_no")
    private String fBillNo;

    /**
     * 单据类型
     */
    @TableField(value = "f_bill_type")
    private String fBillType;

    /**
     * 单据类型ID
     */
    @TableField(value = "f_bill_type_id")
    private String fBillTypeId;

    /**
     * 物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 物料名称
     */
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 数量
     */
    @TableField(value = "f_qty")
    private String fQty;

    /**
     * 业务状态
     */
    @TableField(value = "f_status")
    private String fStatus;

    /**
     * 领料状态
     */
    @TableField(value = "f_pick_mtrl_status")
    private String fPickMtrlStatus;

    /**
     * 合格品入库数量
     */
    @TableField(value = "f_stock_in_qua_aux_qty")
    private String fStockInQuaAuxQty;

    /**
     * BOM版本
     */
    @TableField(value = "f_bom_id")
    private String fBomId;



    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}