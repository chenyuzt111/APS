package com.benewake.system.controller;

import com.benewake.system.annotation.Log;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.entity.vo.AssginRoleVo;
import com.benewake.system.entity.vo.SysRoleQueryVo;
import com.benewake.system.entity.enums.BusinessType;
import com.benewake.system.service.SysRoleService;
import com.benewake.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年08月01 10:28
 * 描 述： TODO
 */
@Api(tags = "角色管理接口")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;

    @Log(title = "角色管理",businessType = BusinessType.ASSGIN)
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("用户分配角色")
    @PostMapping("doAssign")
    @Transactional(rollbackFor = Exception.class)
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo){
        if(StringUtils.isEmpty(assginRoleVo.getUserId()) || sysUserService.getById(assginRoleVo.getUserId())==null){
            return Result.fail().message("用户不存在！");
        }
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysRole.list')")
    @ApiOperation("获取用户角色数据")
    @PostMapping("toAssign")
    public Result  toAssign(@RequestBody SysUser sysUser){
        if(sysUser.getId()==null || sysUserService.getById(sysUser.getId())==null){
            return Result.fail().message("用户不存在！！");
        }
        Map<String,Object> lists = sysRoleService.getRolesByUserId(sysUser);
        return Result.ok(lists);
    }

    @Log(title = "角色管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量删除")
    @PostMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        sysRoleService.removeBatchByIds(ids);
        return Result.ok();
    }

    @Log(title = "角色管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PostMapping("update")
    public Result updateRole(@RequestBody SysRole sysRole){
        if(sysRole.getId() == null){
            return Result.fail().message("修改的角色id不能为空！");
        }
        if("1".equals(sysRole.getId())){
            return Result.fail().message("不允许修改超级管理员");
        }
        if(StringUtils.isEmpty(sysRole.getRoleName())){
            return Result.fail().message("角色名不能为空！");
        }
        if(sysRoleService.isExist(sysRole)){
            return Result.fail().message("角色名已存在!");
        }
        return sysRoleService.updateById(sysRole)?Result.ok():Result.fail().message("修改失败!");
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id查询")
    @PostMapping("findRoleById")
    public Result findRoleById(@RequestBody SysRole sysRole){
        if(sysRole.getId()==null){
            return Result.fail().message("id不能为空！");
        }
        return Result.ok(sysRoleService.getById(sysRole.getId()));
    }

    @Log(title = "角色管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result saveRole(@RequestBody SysRole sysRole){
        if(StringUtils.isEmpty(sysRole.getRoleName())){
            return Result.fail().message("角色名不能为空！");
        }
        if(sysRoleService.isExist(sysRole)){
            return Result.fail().message("角色名已存在!");
        }
        return sysRoleService.save(sysRole)?Result.ok(sysRole):Result.fail();
    }


    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("查询全部角色接口")
    @GetMapping("findAll")
    public Result<List<SysRole>> findAllRoles(){
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    @Log(title = "角色管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("逻辑删除接口")
    @PostMapping("remove")
    public Result removeRole(@RequestBody SysRole sysRole){
        val isSuccess = sysRoleService.removeById(sysRole.getId());
        if(isSuccess){
            return Result.ok();
        }
        return Result.fail();
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("条件查询")
    @PostMapping
    public Result findRoles(@RequestBody SysRoleQueryVo sysRoleQueryVo){
        List<SysRole> roleList = sysRoleService.selectRole(sysRoleQueryVo);
        // 返回
        return Result.ok(roleList);
    }
}
