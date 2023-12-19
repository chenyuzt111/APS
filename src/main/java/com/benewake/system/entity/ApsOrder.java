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
 * @TableName aps_order
 */
@TableName(value ="aps_order")
@Data
public class ApsOrder implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单据编号
     */
    @TableField(value = "bill_no")
    private String billNo;

    /**
     * 单据类型
     */
    @TableField(value = "bill_type")
    private String billType;


    /**
     * 物料编码
     */
    @TableField(value = "material_id")
    private String materialId;

    /**
     * 数量
     */
    @TableField(value = "qty")
    private String qty;

    /**
     * 业务状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 领料状态
     */
    @TableField(value = "pick_mtrl_status")
    private String pickMtrlStatus;

    /**
     * 入库数量
     */
    @TableField(value = "stock_in_qua_aux_qty")
    private String stockInQuaAuxQty;

    /**
     * BOM版本
     */
    @TableField(value = "bom_id")
    private String bomId;

    /**
     * 定制物料编码
     */
    @TableField(value = "dzmaterial_id")
    private String dzmaterialId;

    /**
     * 计划完成时间
     */
    @TableField(value = "planned_completion_time")
    private Date plannedCompletionTime;

    /**
     * 定制物料编码
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