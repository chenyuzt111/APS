package com.benewake.system.entity.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
 * @TableName aps_fim_request
 */
@TableName(value ="aps_fim_request")
@Data
public class ApsFimRequestVo implements Serializable {
    /**
     *
     */
    @ExcelIgnore
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @ExcelProperty("单据编号")
    private String documentNumber;

    /**
     *
     */
    @ExcelProperty(value = "创建人")
    private String creator;

    /**
     *
     */
    @ExcelProperty(value = "物料编码")
    private String materialCode;

    /**
     *
     */
    @ExcelProperty(value = "物料名称")
    private String materialName;

    /**
     *
     */
    @ExcelProperty(value = "客户名称")
    private String customerName;

    /**
     *
     */
    @ExcelProperty(value = "销售员")
    private String salesperson;

    /**
     *
     */
    @ExcelProperty(value = "数量")
    private String quantity;

    /**
     *
     */
    @ExcelProperty(value = "期望发货日期", format = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expectedDeliveryDate;

    /**
     *
     */
    @ExcelProperty(value = "单据类型")
    private String documentType;

    /**
     *
     */
    @ExcelProperty("版本号")
    private String chVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}