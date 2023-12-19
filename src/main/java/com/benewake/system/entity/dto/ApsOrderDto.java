package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
public class ApsOrderDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单据编号
     */
    @ExcelProperty("单据编号")
    private String billNo;

    /**
     * 单据类型
     */
    @ExcelProperty("单据类型")
    private String billType;


    /**
     * 物料编码
     */
    @ExcelProperty("物料编码")
    private String materialId;
    /**
     * 物料名称
     */
    @ExcelProperty("物料名称")
    private String materialName;

    /**
     * 数量
     */
    @ExcelProperty("数量")
    private String qty;

    /**
     * 业务状态
     */
    @ExcelProperty("业务状态")
    private String status;

    /**
     * 领料状态
     */
    @ExcelProperty("领料状态")
    private String pickMtrlStatus;

    /**
     * 入库数量
     */
    @ExcelProperty("入库数量")
    private String stockInQuaAuxQty;

    /**
     * BOM版本
     */
    @ExcelProperty("BOM版本")
    private String bomId;

    /**
     * 定制物料编码
     */
    @ExcelProperty("定制物料编码")
    private String dzmaterialId;

    /**
     * 计划完成时间
     */
    @ExcelProperty("计划完成时间")
    private Date plannedCompletionTime;

    /**
     * 定制物料编码
     */
    @ExcelProperty("定制物料编码")
    private String formName;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}