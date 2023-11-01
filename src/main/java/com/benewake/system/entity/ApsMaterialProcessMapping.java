package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName aps_material_process_mapping
 */
@TableName(value ="aps_material_process_mapping")
@Data
public class ApsMaterialProcessMapping implements Serializable {
    /**
     * 自增id
     */
    @TableField(value = "id")
    private Integer id;

    /**
     * 物料编码
     */
    @TableField(value = "f_material_id")
    private String fMaterialId;

    /**
     * 工序
     */
    @TableField(value = "process")
    private String process;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}