package com.benewake.system.entity.vo;

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
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @ExcelProperty("单据编号")
    @TableField(value = "f_document_number")
    @JsonProperty("documentNumber")
    private String fDocumentNumber;

    /**
     *
     */
    @JsonProperty("creator")
    @TableField(value = "f_creator")
    private String fCreator;

    /**
     *
     */
    @JsonProperty("materialCode")
    @TableField(value = "f_material_code")
    private String fMaterialCode;

    /**
     *
     */
    @JsonProperty("materialName")
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     *
     */
    @JsonProperty("customerName")
    @TableField(value = "f_customer_name")
    private String fCustomerName;

    /**
     *
     */
    @JsonProperty("salesperson")
    @TableField(value = "f_salesperson")
    private String fSalesperson;

    /**
     *
     */
    @JsonProperty("quantity")
    @TableField(value = "f_quantity")
    private String fQuantity;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("expectedDeliveryDate")
    @TableField(value = "f_expected_delivery_date")
    private Date fExpectedDeliveryDate;

    /**
     *
     */
    @JsonProperty("documentType")
    @TableField(value = "f_document_type")
    private String fDocumentType;

    /**
     *
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}