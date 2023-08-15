package com.benewake.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Lcs
 * @since 2023年08月14 16:13
 * 描 述： TODO
 */
@Data
@TableName("aps_col_table")
public class ApsCol {
    @TableField("col_id")
    private String colId;
    @TableField("menu_id")
    private String menuId;
    @TableField("col_name")
    private String colName;
    @TableField("col_name_ENG")
    private String colNameEng;
    @TableField("col_name_CN")
    private String colNameCn;
}
