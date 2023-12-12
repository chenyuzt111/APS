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
 * @TableName aps_packaging_test
 */
@TableName(value = "aps_packaging_test")
@Data
public class ApsPackagingTestDto implements Serializable {
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
    @ColumnWidth(20) // Set the column width to 20 characters
    private String productionOrderNumber;

    /**
     * 物料编码
     */
    @ExcelProperty("物料编码")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String materialCode;

    /**
     * 物料名称
     */
    @ExcelProperty("物料名称")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String materialName;

    /**
     * 本次包装终检完成数
     */
    @ExcelProperty("本次包装终检完成数")
    @ColumnWidth(25) // Set the column width to 25 characters
    private String burnInCompletionQuantity;

    /**
     * 包装终检合格数
     */
    @ExcelProperty("包装终检合格数")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String burnQualifiedCount;

    /**
     * 包装终检不合格数
     */
    @ExcelProperty("包装终检不合格数")
    @ColumnWidth(20) // Set the column width to 20 characters
    private String unBurnQualifiedCount;

    /**
     * 订单总数
     */
    @ExcelProperty("订单总数")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String totalNumber;

    /**
     * 版本号
     */
    @ExcelProperty("版本号")
    @ColumnWidth(15) // Set the column width to 15 characters
    private String version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
