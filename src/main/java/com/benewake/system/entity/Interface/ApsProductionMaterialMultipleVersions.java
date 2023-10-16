package com.benewake.system.entity.Interface;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.ApsProductionMaterial;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName aps_production_material
 */
@TableName(value ="aps_production_material")
@Data
public class ApsProductionMaterialMultipleVersions extends ApsProductionMaterial implements Serializable {


    private String chVersionName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}