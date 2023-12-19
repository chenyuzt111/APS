package com.benewake.system.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Lcs
 */
@Data
@ApiModel(description = "用户")
@TableName("sys_user")
public class SysUser extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户名")
	@TableField("username")
	private String username;

	@ApiModelProperty(value = "密码")
	@TableField("password")
	private String password;

	@ApiModelProperty(value = "邮箱")
	@TableField("email")
	private String email;

	@ApiModelProperty(value = "部门id")
	@TableField("dept_id")
	private String deptId;

	@ApiModelProperty(value = "状态（1：正常 0：停用）")
	@TableField("status")
	private Integer status;

	// 角色列表
	@TableField(exist = false)
	private List<SysRole> roleList;
	// 部门
	@TableField(exist = false)
	private String deptName;
}

