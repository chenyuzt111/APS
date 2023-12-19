package com.benewake.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SortVo {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    @TableField(value = "col_id")
    private Integer colId;


    @TableField(value = "ch_col_name")
    private String chColName;

    /**
     *
     */
    @TableField(value = "vo_col_name")
    private String voColName;


    @TableField(value = "value_operator")
    private String valueOperator;

    /**
     *
     */
    @TableField(value = "col_value")
    private String colValue;

    /**
     *
     */
    @TableField(value = "col_seq")
    private Integer colSeq;

}
