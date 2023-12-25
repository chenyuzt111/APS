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
 * @TableName aps_mes_total
 */
@TableName(value ="aps_mes_total")
@Data
public  class ApsMesTotalDto implements Serializable {
    /**
     * 唯一标识符
     */
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 订单总数
     */
    @ExcelProperty("订单总数")
    private String totalNumber;

    /**
     * 本次完成数
     */
    @ExcelProperty("本次完成数")
    private String burnInCompletionQuantity;

    /**
     * 合格数
     */
    @ExcelProperty("合格数")
    private String burnQualifiedCount;

    /**
     * 不合格数
     */
    @ExcelProperty("不合格数")
    private String unBurnQualifiedCount;

    /**
     * 工装编号
     */
    @ExcelProperty("工装编号")
    private String burnFixtureNumber;

    /**
     * 机器id
     */
    @ExcelProperty("机器id")
    private Integer burnFixtureId;

    /**
     * 工序
     */
    @ExcelProperty("工序")
    private String process;

    /**
     * 工件
     */
    @ExcelProperty("工件")
    private String workpiece;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
