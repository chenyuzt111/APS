package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.system.ApsCol;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.entity.vo.AssginRoleVo;
import com.benewake.system.entity.vo.SysRoleQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年07月29 16:37
 * 描 述： TOD
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 条件查询角色
     * @param sysRoleQueryVo
     * @return
     */
    List<SysRole> selectRole(SysRoleQueryVo sysRoleQueryVo);

    /**
     * 判断角色名或角色号是否存在
     * @param sysRole
     * @return
     */
    boolean isExist(SysRole sysRole);

    /**
     * 根据用户id获取对应的角色列表
     * 包括全部角色和当前用户拥有角色
     *
     * @param sysUser
     * @return
     */
    Map<String,Object> getRolesByUserId(SysUser sysUser);

    /**
     * 给用户分配角色
     *
     * @param assginRoleVo
     * @return
     */
    Integer doAssign(AssginRoleVo assginRoleVo);

    /**
     * 获取用户所属的角色列表
     * @param id
     * @return
     */
    List<SysRole> getUserRoles(String id);

}
