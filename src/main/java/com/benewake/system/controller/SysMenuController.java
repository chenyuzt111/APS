package com.benewake.system.controller;

import com.benewake.system.annotation.Log;
import com.benewake.system.entity.Result;
import com.benewake.system.entity.system.SysMenu;
import com.benewake.system.entity.vo.AssginMenuVo;
import com.benewake.system.entity.enums.BusinessType;
import com.benewake.system.service.SysMenuService;
import com.benewake.system.utils.HostHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author lcs
 * @since 2023-08-02 01:44:33
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private HostHolder hostHolder;

    @Log(title = "菜单管理",businessType = BusinessType.ASSGIN)
    @PreAuthorize("hasAuthority('bnt.sysRole.assignAuth')")
    @ApiOperation("给角色分配菜单权限")
    @PostMapping("doAssign")
    @Transactional(rollbackFor = Exception.class)
    public Result doAssign(@RequestBody AssginMenuVo assginMenuVo){
        sysMenuService.doAssign(assginMenuVo);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.list')")
    @ApiOperation("根据角色获取菜单")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable String roleId){
        List<SysMenu> list = sysMenuService.findMenuByRoleId(roleId);
        return Result.ok(list);
    }
    @Log(title = "菜单管理",businessType = BusinessType.INSERT)
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.add')")
    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody SysMenu sysMenu){
        if (sysMenu.getParentId()==null || sysMenu.getParentId()<0){
            sysMenu.setParentId(0L);
        }
        if(StringUtils.isEmpty(sysMenu.getName())){
            return Result.fail().message("菜单名称不能为空！");
        }
        if(sysMenu.getType()==null || sysMenu.getType()<0 || sysMenu.getType()>3){
            return Result.fail().message("菜单类型无效！请检查");
        }
        sysMenuService.save(sysMenu);
        return Result.ok();
    }
    @Log(title = "菜单管理",businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.update')")
    @ApiOperation("修改菜单")
    @PostMapping("update")
    public Result update(@RequestBody SysMenu sysMenu){
        if(sysMenu.getId()==null){
            return Result.fail();
        }
        if (sysMenu.getParentId()==null || sysMenu.getParentId()<0){
            sysMenu.setParentId(0L);
        }
        if(StringUtils.isEmpty(sysMenu.getName())){
            return Result.fail().message("菜单名称不能为空！");
        }
        if(sysMenu.getType()==null || sysMenu.getType()<0 || sysMenu.getType()>2){
            return Result.fail().message("菜单类型无效！请检查");
        }
        sysMenuService.updateById(sysMenu);
        return Result.ok();
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.list')")
    @ApiOperation("菜单列表")
    @PostMapping("findNodes")
    public Result findNodes(@RequestBody SysMenu sysMenu){
        List<SysMenu> list = sysMenuService.findNodes(sysMenu);
        return Result.ok(list);
    }

    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.list')")
    @ApiOperation("根据id查询")
    @GetMapping("findNode/{id}")
    public Result findNode(@PathVariable String id){
        SysMenu sysMenu = sysMenuService.getById(id);
        return Result.ok(sysMenu);
    }
    @Log(title = "菜单管理",businessType = BusinessType.DELETE)
    @PreAuthorize("hasAnyAuthority('bnt.sysMenu.remove')")
    @ApiOperation("删除菜单")
    @DeleteMapping("delete/{id}")
    public Result remove(@PathVariable String id){
        return sysMenuService.removeMenuById(id)?Result.ok() : Result.build(null,201,"请先删除子菜单！");
    }
}

