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
 * @TableName aps_purchase_requests_orders
 */
@TableName(value ="aps_purchase_requests_orders")
@Data
public class ApsPurchaseRequestsOrders implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @TableField(value = "bill_no")
    private String billNo;


    /**
     * 物料编码
     */
    @TableField(value = "material_id")
    private String materialId;

    /**
     * 物料名称
     */
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 批准数量
     */
    @TableField(value = "base_unit_qty")
    private String baseUnitQty;

    /**
     * 到货日期
     */
    @TableField(value = "arrival_date")
    private Date arrivalDate;

    /**
     * 表单名称
     */
    @TableField(value = "form_name")
    private String formName;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}