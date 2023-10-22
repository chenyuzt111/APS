package com.benewake.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Lcs
 * @since 2023年08月12 10:55
 * 描 述： T
 */
@Data
@TableName("sys_role_dept")
public class SysRoleDept {
    /** 角色ID */
    @TableField("role_id")
    private Long roleId;

    /** 部门ID */
    @TableField("dept_id")
    private Long deptId;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("roleId", getRoleId())
                .append("deptId", getDeptId())
                .toString();
    }
}
