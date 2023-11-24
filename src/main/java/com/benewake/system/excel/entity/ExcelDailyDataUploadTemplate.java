package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ExcelDailyDataUploadTemplate implements Serializable {



    @ColumnWidth(12)
    @ExcelProperty("订单编号")
    private String orderNumber;

    /**
     *
     */
    @ColumnWidth(12)
    @ExcelProperty("物料编码")
    private String materialCode;
//
//    /**
//     *
//     */
//    @ColumnWidth(22)
//    @ExcelProperty("物料名称")
//    private String materialName;


    /**
     *
     */
    @ColumnWidth(20)
    @ExcelProperty("工序名称")
    private String processName;


    /**
     *
     */
    @ColumnWidth(12)
    @ExcelProperty("总数量")
    private Integer totalQuantity;

    /**
     *
     */
    @ColumnWidth(12)
    @ExcelProperty("完成数量")
    private Integer completedQuantity;

    /**
     *
     */
    @ColumnWidth(20)
    @ExcelProperty("产能(秒/台/人)")
    private Integer capacityPsPuPp;

    /**
     *
     */
    @ColumnWidth(12)
    @ExcelProperty("剩余数量")
    private Integer remainingQuantity;

    /**
     *
     */
    @ColumnWidth(12)
    @ExcelProperty("剩余产能")
    private Integer remainingCapacity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
