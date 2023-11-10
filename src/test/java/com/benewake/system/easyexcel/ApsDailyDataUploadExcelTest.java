package com.benewake.system.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_daily_data_upload
 */
@TableName(value ="aps_daily_data_upload")
@Data
public class ApsDailyDataUploadExcelTest implements Serializable {
    @ExcelProperty("订单编号")
    private String orderNumber;

    @ExcelProperty("物料编码")
    private String materialCode;

    @ExcelProperty("物料名称")
    private String materialName;

    @ExcelProperty("工序")
    private String process;

    @ExcelProperty("总数量")
    private String totalQuantity;

    @ExcelProperty("完成数量")
    private String completedQuantity;

    @ExcelProperty("产能(秒/台/人)")
    private String capacity;

    @ExcelProperty("剩余数量")
    private String remainingQuantity;

    @ExcelProperty("剩余产能")
    private String remainingCapacity;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}