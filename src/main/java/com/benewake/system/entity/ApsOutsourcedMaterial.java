package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_outsourced_material
 */
@TableName(value ="aps_outsourced_material")
@Data
public class ApsOutsourcedMaterial implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;
    /**
     * 产品编码
     */
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 委外订单编号
     */
    @TableField(value = "f_sub_req_bill_no")
    private String fSubReqBillNo;

    /**
     * 子项物料编码
     */
    @TableField(value = "f_material_id2")
    private String fMaterialId2;
    /**
     * 子项物料编码
     */
    @TableField(value = "f_material_name2")
    private String fMaterialName2;

    /**
     * 子项类型
     */
    @TableField(value = "f_material_type")
    private String fMaterialType;

    /**
     * 应发数量
     */
    @TableField(value = "f_must_qty")
    private String fMustQty;

    /**
     * 已领数量
     */
    @TableField(value = "f_picked_qty")
    private String fPickedQty;

    /**
     * 良品退料数量
     */
    @TableField(value = "f_good_return_qty")
    private String fGoodReturnQty;

    /**
     * 作业不良退料数量
     */
    @TableField(value = "f_process_defect_return_qty")
    private String fProcessDefectReturnQty;

    /**
     * 版本号
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}