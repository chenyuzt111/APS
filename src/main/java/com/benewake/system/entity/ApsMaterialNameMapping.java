package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @TableName aps_data_source
 */
@TableName(value ="aps_material_name_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApsMaterialNameMapping implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 
     */
    @TableField(value = "f_material_name")
    private String fMaterialName;

    /**
     * 
     */
    @TableField(value = "f_item_model")
    private String fItemModel;

    /**
     * 
     */
    @TableField(value = "f_data_source")
    private String fDataSource;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}