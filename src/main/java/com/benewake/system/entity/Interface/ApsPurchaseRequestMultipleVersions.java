package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsPurchaseRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_purchase_request
 */
@TableName(value ="aps_purchase_request")
@Data
public class ApsPurchaseRequestMultipleVersions extends ApsPurchaseRequest implements Serializable {
    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}