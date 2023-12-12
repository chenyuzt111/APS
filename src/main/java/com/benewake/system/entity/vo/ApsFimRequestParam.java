package com.benewake.system.entity.vo;

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
public class ApsFimRequestParam implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_document_number")
    private String documentNumber;

    /**
     * 
     */
    @TableField(value = "f_material_code")
    private String materialCode;

    /**
     * 
     */
    @TableField(value = "f_customer_name")
    private String customerName;

    /**
     * 
     */
    @TableField(value = "f_salesperson")
    private String salesperson;

    @JsonProperty("creator")
    private String fCreator;

    /**
     * 
     */
    @TableField(value = "f_quantity")
    private String quantity;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "f_expected_delivery_date")
    private Date expectedDeliveryDate;

    /**
     * 
     */
    @TableField(value = "f_document_type")
    private String documentType;

    /**
     * 
     */
    @TableField(value = "version")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}