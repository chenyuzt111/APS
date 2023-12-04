package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName aps_tfmini_s_pcba_burn
 */
@TableName(value ="aps_tfmini_s_pcba_burn")
@Data
public class ApsTfminiSPcbaBurnDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 生产订单编号
     */
    @ColumnWidth(24)
    @ExcelProperty("生产订单编号")
    @TableField(value = "production_order_number")
    private String productionOrderNumber;

    /**
     * 物料编码
     */
    @ColumnWidth(12)
    @ExcelProperty("物料编码")
    @TableField(value = "material_code")
    private String materialCode;

    /**
     * 物料名称
     */
    @ColumnWidth(28)
    @ExcelProperty("物料名称")
    @TableField(value = "material_name")
    private String materialName;

    /**
     * 本次烧录完成数
     */
    @ColumnWidth(20)
    @ExcelProperty("本次烧录完成数")
    @TableField(value = "burn_in_completion_quantity")
    private String burnInCompletionQuantity;

    /**
     * 烧录合格数
     */
    @ColumnWidth(16)
    @ExcelProperty("烧录合格数")
    @TableField(value = "burn_qualified_count")
    private String burnQualifiedCount;

    /**
     * 烧录不合格数
     */
    @ColumnWidth(20)
    @ExcelProperty("烧录不合格数")
    @TableField(value = "un_burn_qualified_count")
    private String unburnQualifiedCount;

    /**
     * 烧录工装编号
     */
    @ColumnWidth(20)
    @ExcelProperty("烧录工装编号")
    @TableField(value = "burn_fixture_number")
    private String burnFixtureNumber;

    /**
     * 机器id
     */
    @ColumnWidth(12)
    @ExcelProperty("机器id")
    @TableField(value = "burn_fixture_id")
    private Integer burnFixtureId;

    /**
     * 订单总数
     */
    @ColumnWidth(12)
    @ExcelProperty("订单总数")
    @TableField(value = "total_number")
    private String totalNumber;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    @TableField(value = "version")
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
