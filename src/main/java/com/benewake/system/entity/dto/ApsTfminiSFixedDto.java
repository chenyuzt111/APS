package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_tfmini_s_fixed
 */
@TableName(value ="aps_tfmini_s_fixed")
@Data
public class ApsTfminiSFixedDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 生产订单编号
     */
    @ExcelProperty("生产订单编号")
    private String productionOrderNumber;

    /**
     * 物料编码
     */
    @ExcelProperty("物料编码")
    private String materialCode;

    /**
     * 物料名称
     */
    @ExcelProperty("物料名称")
    private String materialName;

    /**
     * 本次安装完成数
     */
    @ExcelProperty("次安装完成数")
    private String burnInCompletionQuantity;

    /**
     * 安装合格数84950991
     */
    @ExcelProperty("安装合格数")
    private String burnQualifiedCount;

    /**
     * 用料清单编号
     */
    @ExcelProperty("用料清单编号")
    private String burnFixtureNumber;

    /**
     * 
     */
    @ExcelProperty("机器id")
    private Integer burnFixtureId;

    /**
     * 订单总数
     */
    @ExcelProperty("订单总数")
    private String totalNumber;

    /**
     * 安装不合格数84950990
     */
    @ExcelProperty("安装不合格数")
    private String unBurnQualifiedCount;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}