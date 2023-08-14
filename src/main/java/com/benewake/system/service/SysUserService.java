package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import com.benewake.system.entity.vo.LoginVo;
import com.benewake.system.entity.vo.UpdatePwdVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lcs
 * @since 2023-08-01 06:36:31
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 条件查询用户
     * @param sysUser
     * @return
     */
    List<SysUser> selectUser(SysUser sysUser);

    /**
     * 修改用户状态
     * @param id
     * @param status
     * @return
     */
    boolean updateStatus(String id, Integer status);

    /**
     * 添加新用户
     * @param sysUser
     * @return
     */
    Map<String, Object> addSysUser(SysUser sysUser);

    /**
     * 修改用户密码
     * @param updatePwdVo
     * @return
     */
    Map<String, Object> updatePassword(UpdatePwdVo updatePwdVo);

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    Map<String, Object> login(LoginVo loginVo);

    /**
     * 根据用户名获取对应的用户信息
     * @param username
     * @return
     */
    SysUser getUserInfoByUsername(String username);

    /**
     * 根据用户名获取用户信息 包括菜单 按钮权限等
     * @param username
     * @return
     */
    Map<String, Object> getUserInfo(String id,String username);

    /**
     * 根据角色id获取有对应角色的用户信息
     *
     * @param sysRole
     * @return
     */
    List<SysUser> getUsersByRoleId(SysRole sysRole);
}
