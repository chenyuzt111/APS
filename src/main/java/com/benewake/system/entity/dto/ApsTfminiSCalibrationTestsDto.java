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
 * @TableName aps_tfmini_s_calibration_tests
 */
@TableName(value = "aps_tfmini_s_calibration_tests")
@Data
public class ApsTfminiSCalibrationTestsDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 生产订单编号
     */
    @ExcelProperty("生产订单编号")
    @TableField(value = "production_order_number")
    @ColumnWidth(20) // Set the column width to 20 characters
    private String productionOrderNumber;

    /**
     * 物料编码
     */
    @ExcelProperty("物料编码")
    @TableField(value = "material_code")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String materialCode;

    /**
     * 物料名称
     */
    @ExcelProperty("物料名称")
    @TableField(value = "material_name")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String materialName;

    /**
     * 本次校准测试完成数
     */
    @ExcelProperty("本次校准测试完成数")
    @TableField(value = "burn_in_completion_quantity")
    @ColumnWidth(25) // Set the column width to 25 characters
    private String burnInCompletionQuantity;

    /**
     * 校准合格数
     */
    @ExcelProperty("校准合格数")
    @TableField(value = "burn_qualified_count")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String burnQualifiedCount;

    /**
     * 校准不合格数
     */
    @ExcelProperty("校准不合格数")
    @TableField(value = "unburn_qualified_count")
    @ColumnWidth(20) // Set the column width to 20 characters
    private String unBurnQualifiedCount;

    /**
     * 测试工装编号
     */
    @ExcelProperty("测试工装编号")
    @TableField(value = "burn_fixture_number")
    @ColumnWidth(20) // Set the column width to 20 characters
    private String burnFixtureNumber;

    /**
     * 订单总数
     */
    @ExcelProperty("订单总数")
    @TableField(value = "total_number")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String totalNumber;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    @TableField(value = "version")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
