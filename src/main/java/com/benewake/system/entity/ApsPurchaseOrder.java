package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName aps_purchase_order
 */
@TableName(value ="aps_purchase_order")
@Data
public class ApsPurchaseOrder implements Serializable {
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
     * 物料名称
     */
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 剩余收料数量
     */
    @TableField(value = "f_remain_receive_qty")
    private String fRemainReceiveQty;

    /**
     * 交货日期
     */
    @TableField(value = "f_delivery_date")
    private String fDeliveryDate;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}