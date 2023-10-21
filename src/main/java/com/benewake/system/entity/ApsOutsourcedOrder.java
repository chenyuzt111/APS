package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_outsourced_order
 */
@TableName(value ="aps_outsourced_order")
@Data
public class ApsOutsourcedOrder implements Serializable {
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
     * 物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;
    /**
     * 物料编码
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
     * 入库数量
     */
    @TableField(value = "f_stock_in_qty")
    private String fStockInQty;

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