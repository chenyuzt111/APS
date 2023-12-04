package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsOutsourcedOrder;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_outsourced_order
 */
@TableName(value ="aps_outsourced_order")
@Data
public class ApsOutsourcedOrderMultipleVersions extends ApsOutsourcedOrder implements Serializable {
    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}