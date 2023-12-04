package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsOutsourcedMaterial;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_outsourced_material
 */
@TableName(value ="aps_outsourced_material")
@Data
public class ApsOutsourcedMaterialMultipleVersions extends ApsOutsourcedMaterial implements Serializable {
    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}