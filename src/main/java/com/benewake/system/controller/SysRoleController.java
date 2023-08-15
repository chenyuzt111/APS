package com.benewake.system.controller;

import com.benewake.system.annotation.Log;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.vo.AssginRoleVo;
import com.benewake.system.entity.vo.SysRoleQueryVo;
import com.benewake.system.entity.enums.BusinessType;
import com.benewake.system.service.SysRoleService;
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

    @Log(title = "角色管理",businessType = BusinessType.ASSGIN)
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("用户分配角色")
    @PostMapping("doAssign")
    @Transactional(rollbackFor = Exception.class)
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo){
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysRole.list')")
    @ApiOperation("获取用户角色数据")
    @GetMapping("toAssign/{userId}")
    public Result  toAssign(@PathVariable String userId){
        Map<String,Object> lists = sysRoleService.getRolesByUserId(userId);
        return Result.ok(lists);
    }

    @Log(title = "角色管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
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
    @DeleteMapping("remove/{id}")
    public Result removeRole(@PathVariable("id")String id){
        val isSuccess = sysRoleService.removeById(id);
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
