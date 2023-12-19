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
 * @TableName aps_purchase_requests_orders
 */
@TableName(value = "aps_purchase_requests_orders")
@Data
public class ApsPurchaseRequestsOrdersDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @ExcelProperty("单据编号")
    private String billNo;
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
     * 批准数量
     */
    @ExcelProperty("批准数量")
    private String baseUnitQty;

    /**
     * 到货日期
     */
    @ExcelProperty("到货日期")
    private Date arrivalDate;

    /**
     * 表单名称
     */
    @ExcelProperty("表单名称")
    private String formName;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}