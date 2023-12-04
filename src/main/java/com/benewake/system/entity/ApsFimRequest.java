package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName aps_fim_request
 */
@TableName(value ="aps_fim_request")
@Data
public class ApsFimRequest implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_document_number")
    private String fDocumentNumber;

    /**
     * 
     */
    @TableField(value = "f_creator")
    private String fCreator;

    /**
     * 
     */
    @TableField(value = "f_material_code")
    private String fMaterialCode;

    /**
     * 
     */
//    @TableField(value = "f_material_name")
//    private String fMaterialName;

    /**
     * 
     */
    @TableField(value = "f_customer_name")
    private String fCustomerName;

    /**
     * 
     */
    @TableField(value = "f_salesperson")
    private String fSalesperson;

    /**
     * 
     */
    @TableField(value = "f_quantity")
    private String fQuantity;

    /**
     * 
     */
    @TableField(value = "f_expected_delivery_date")
    private Date fExpectedDeliveryDate;

    /**
     * 
     */
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