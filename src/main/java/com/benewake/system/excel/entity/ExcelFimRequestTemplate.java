package com.benewake.system.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName aps_fim_request
 */
@Data
public class ExcelFimRequestTemplate implements Serializable {

    /**
     *
     */
    @ExcelProperty(value = "单据编号")
    @ColumnWidth(20) // 适当设置列宽度
    private String fDocumentNumber;

    /**
     *
     */
    @ExcelProperty(value = "创建人")
    @ColumnWidth(15) // 适当设置列宽度
    private String fCreator;

    /**
     *
     */
    @ExcelProperty(value = "物料编码")
    @ColumnWidth(15) // 适当设置列宽度
    private String fMaterialCode;

    /**
     *
     */
    @ExcelProperty(value = "客户名称")
    @ColumnWidth(20) // 适当设置列宽度
    private String fCustomerName;

    /**
     *
     */
    @ExcelProperty(value = "销售员")
    @ColumnWidth(15) // 适当设置列宽度
    private String fSalesperson;

    /**
     *
     */
    @ExcelProperty(value = "数量")
    @ColumnWidth(10) // 适当设置列宽度
    private String fQuantity;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "期望发货日期", format = "yyyy-MM-dd")
    @ColumnWidth(15) // 适当设置列宽度
    private String fExpectedDeliveryDate;

    /**
     *
     */
    @ExcelProperty(value = "单据类型")
    @ColumnWidth(15) // 适当设置列宽度
    private String fDocumentType;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
