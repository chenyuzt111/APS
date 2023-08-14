package com.benewake.system.entity.system;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benewake.system.entity.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "部门")
@TableName("sys_dept")
public class SysDept extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "部门名称")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "上级部门id")
	@TableField("parent_id")
	private Long parentId;

	@ApiModelProperty(value = "祖级列表")
	@TableField("ancestors")
	private String ancestors;

	@ApiModelProperty(value = "排序")
	@TableField("sort_value")
	private Integer sortValue;

	@ApiModelProperty(value = "负责人")
	@TableField("leader")
	private String leader;

	@ApiModelProperty(value = "电话")
	@TableField("phone")
	private String phone;

	@ApiModelProperty(value = "下级部门")
	@TableField(exist = false)
	private List<SysDept> children;

}