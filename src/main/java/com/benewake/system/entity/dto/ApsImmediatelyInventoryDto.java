package com.benewake.system.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName aps_immediately_inventory
 */
@TableName(value ="aps_immediately_inventory")
@Data
public class ApsImmediatelyInventoryDto implements Serializable {
    /**
     * 自增ID
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @ColumnWidth(14)
    @ExcelProperty("物料编码")
    @TableField(value = "f_material_id")
    private String materialId;

    /**
     * 物料编码
     */
    @ColumnWidth(24)
    @ExcelProperty("物料名称")
    @TableField(value = "f_material_name")
    private String materialName;

    /**
     * 仓库名称
     */
    @ColumnWidth(15)
    @ExcelProperty("仓库名称")
    @TableField(value = "f_stock_name")
    private String stockName;

    /**
     * 库存量(基本单位)
     */
    @ColumnWidth(24)
    @ExcelProperty("库存量(基本单位)")
    @TableField(value = "f_base_qty")
    private Integer baseQty;

    /**
     * 可用量(主单位)
     */
    @ColumnWidth(24)
    @ExcelProperty("可用量(主单位)")
    @TableField(value = "f_avb_qty")
    private Integer avbQty;

    /**
     * 批号
     */
    @ExcelProperty("批号")
    @TableField(value = "f_lot")
    private String lot;

    /**
     * 有效期至
     */
    @ColumnWidth(24)
    @ExcelProperty("有效期至")
    @TableField(value = "f_expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 指定日期格式
    private Date expiryDate;

    /**
     * 版本号
     */
    @ColumnWidth(10)
    @ExcelProperty("版本号")
    @TableField(value = "version")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
