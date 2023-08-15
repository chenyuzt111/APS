package com.benewake.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Lcs
 * @since 2023年08月14 15:52
 * 描 述： TODO
 */
@Data
@TableName("aps_role_col")
public class ApsRoleCol {
    @TableField("role_id")
    private String roleId;
    @TableField("col_id")
    private String colId;
}
