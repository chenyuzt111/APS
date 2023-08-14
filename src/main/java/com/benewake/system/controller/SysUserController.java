package com.benewake.system.controller;

import com.benewake.system.annotation.Log;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.entity.vo.UpdatePwdVo;
import com.benewake.system.enums.BusinessType;
import com.benewake.system.service.SysRoleService;
import com.benewake.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lcs
 * @since 2023-08-01 06:36:31
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService roleService;

    @PreAuthorize("hasAnyAuthority('bnt.sysUser.list')")
    @ApiOperation("分配了角色的用户集合")
    @PostMapping("allocatedRole")
    public Result allowRoleUsers(@RequestBody SysRole sysRole){
        if(StringUtils.isEmpty(sysRole.getId())){
            return Result.fail().message("请选择用户！");
        }
        List<SysUser> users = sysUserService.getUsersByRoleId(sysRole);
        return Result.ok(users);
    }

    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.update')")
    @ApiOperation("用户状态修改接口")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,@PathVariable Integer status){
        if(StringUtils.isEmpty(id) || "1".equals(id)){
            return Result.fail().message("用户不存在或为超级管理员，无法修改！");
        }
        sysUserService.updateStatus(id,status);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysUser.list')")
    @ApiOperation("条件查询用户接口")
    @PostMapping
    public Result selectUser(@RequestBody SysUser sysUser){
        List<SysUser> userList = sysUserService.selectUser(sysUser);
        return Result.ok(userList);
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysUser.list')")
    @ApiOperation("根据id查询接口")
    @GetMapping("getUser/{id}")
    public Result getUserById(@PathVariable("id") String id){
        // 用户基本信息
        SysUser sysUser = sysUserService.getById(id);
        // 用户角色信息
        sysUser.setRoleList(roleService.getUserRoles(id));
        return sysUser!=null?Result.ok(sysUser):Result.fail();
    }
    @Log(title = "用户管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.add')")
    @ApiOperation("添加用户接口")
    @PostMapping("save")
    public Result saveSysUser(@RequestBody SysUser sysUser){
        // 添加用户
        Map<String,Object> res = sysUserService.addSysUser(sysUser);
        return res.size()==0?Result.ok() : Result.fail(res.get("error"));
    }

    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.update')")
    @ApiOperation("修改用户基本信息接口")
    @PostMapping("update")
    public Result updateSysUser(@RequestBody SysUser sysUser){
        if(StringUtils.isEmpty(sysUser.getId())){
            return Result.fail().message("请选择用户！");
        }
        if("1".equals(sysUser.getId())){
            return Result.fail().message("不允许修改超级管理员！");
        }
        if(sysUser.getUsername()!=null && sysUser.getUsername().length() == 0){
            return Result.fail().message("用户名不能为空！");
        }
        sysUser.setPassword(null);
        return sysUserService.updateById(sysUser)?Result.ok() : Result.fail();
    }

    @ApiOperation("修改用户密码接口")
    @PostMapping("updatePwd")
    public Result updatePassword(@RequestBody UpdatePwdVo updatePwdVo){
        Map<String,Object> res = sysUserService.updatePassword(updatePwdVo);
        if(res.size() == 0){
            // 修改成功 清除token（前端清除已存储的token信息） 重新登录

            return Result.ok();
        }else{
            return Result.fail().message((String) res.get("error"));
        }
    }

    @Log(title = "用户管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.remove')")
    @ApiOperation("逻辑删除接口")
    @DeleteMapping("remove/{id}")
    public Result removeUser(@PathVariable String id){
        if(id==null){
            return Result.fail().message("请选择用户！");
        }
        if("1".equals(id)){
            return Result.fail().message("不允许修改超级管理员！");
        }
        return sysUserService.removeById(id)?Result.ok():Result.fail();
    }
    @Log(title = "用户管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAnyAuthority('bnt.sysUser.remove')")
    @ApiOperation("批量删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<String> ids){
        for(String id :ids){
            if("1".equals(id)){
                return Result.fail().message("不允许修改超级管理员！");
            }
        }
        sysUserService.removeBatchByIds(ids);
        return Result.ok();
    }

}
