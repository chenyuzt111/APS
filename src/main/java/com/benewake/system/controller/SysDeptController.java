package com.benewake.system.controller;

import com.benewake.system.annotation.Log;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysDept;
import com.benewake.system.enums.BusinessType;
import com.benewake.system.service.SysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 组织机构 前端控制器
 * </p>
 *
 * @author lcs
 * @since 2023-08-04 05:22:18
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/admin/system/sysDept")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @Log(title = "部门管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAnyAuthority('bnt.sysDept.add')")
    @ApiOperation("添加部门")
    @PostMapping("save")
    public Result save(@RequestBody SysDept sysDept){
        if (sysDept.getParentId()==null || sysDept.getParentId()<0){
            sysDept.setParentId(0L);
        }
        if(StringUtils.isEmpty(sysDept.getName())){
            return Result.fail().message("部门名称不能为空！");
        }
        sysDeptService.insertDept(sysDept);
        return Result.ok();
    }

    @Log(title = "部门管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAnyAuthority('bnt.sysDept.update')")
    @ApiOperation("修改部门")
    @PostMapping("update")
    public Result updateDept(@RequestBody SysDept sysDept){
        if(sysDept.getId()==null){
            return Result.fail().message("未选择部门！");
        }
        if (sysDept.getParentId()==null || sysDept.getParentId()<0){
            sysDept.setParentId(0L);
        }
        if(StringUtils.isEmpty(sysDept.getName())){
            return Result.fail().message("部门名称不能为空！");
        }
        sysDeptService.updateById(sysDept);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysDept.list')")
    @ApiOperation("部门管理信息")
    @PostMapping("list")
    public Result deptsList(@RequestBody SysDept sysDept){
        List<SysDept> list = sysDeptService.selectDeptList(sysDept);
        return Result.ok(list);
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysDept.list')")
    @ApiOperation("部门树形信息列表")
    @GetMapping("findDepts")
    public Result findDepts(){
        List<SysDept> list = sysDeptService.findDepts();
        return Result.ok(list);
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysDept.list')")
    @ApiOperation("根据id查询")
    @GetMapping("findDept/{id}")
    public Result findDept(@PathVariable String id){
        return Result.ok(sysDeptService.getById(id));
    }
    @Log(title = "部门管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAnyAuthority('bnt.sysDept.remove')")
    @ApiOperation("删除部门")
    @DeleteMapping("delete/{id}")
    public Result removeDept(@PathVariable String id){
        return sysDeptService.removeDeptById(id)?Result.ok() : Result.fail();
    }

}
