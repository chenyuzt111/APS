package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_receive_notice
 */
@TableName(value ="aps_receive_notice")
@Data
public class ApsReceiveNotice implements Serializable {
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
     * 物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 实收数量
     */
    @TableField(value = "f_must_qty")
    private String fMustQty;

    /**
     * 检验数量
     */
    @TableField(value = "f_check_qty")
    private String fCheckQty;

    /**
     * 合格数量
     */
    @TableField(value = "f_receive_qty")
    private String fReceiveQty;

    /**
     * 让步接收数量(基本单位)
     */
    @TableField(value = "f_csn_receive_base_qty")
    private String fCsnReceiveBaseQty;

    /**
     * 入库数量
     */
    @TableField(value = "f_in_stock_qty")
    private String fInStockQty;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}