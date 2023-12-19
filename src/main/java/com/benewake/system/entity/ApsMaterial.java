package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_material
 */
@TableName(value ="aps_material")
@Data
public class ApsMaterial implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品编码
     */
    @TableField(value = "material_id")
    private String materialId;

    /**
     * 物料名称
     */
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 委外订单编号
     */
    @TableField(value = "sub_req_bill_no")
    private String subReqBillNo;

    /**
     * 子项物料编码
     */
    @TableField(value = "material_id2")
    private String materialId2;

    /**
     * 子项物料名称
     */
    @TableField(value = "material_name2")
    private String materialName2;

    /**
     * 子项类型
     */
    @TableField(value = "material_type")
    private String materialType;

    /**
     * 应发数量
     */
    @TableField(value = "must_qty")
    private String mustQty;

    /**
     * 已领数量
     */
    @TableField(value = "picked_qty")
    private String pickedQty;

    /**
     * 良品退料数量
     */
    @TableField(value = "good_return_qty")
    private String goodReturnQty;

    /**
     * 作业不良退料数量
     */
    @TableField(value = "process_defect_return_qty")
    private String processDefectReturnQty;

    /**
     * 定制物料编码
     */
    @TableField(value = "dzmaterial_id")
    private String dzmaterialId;

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