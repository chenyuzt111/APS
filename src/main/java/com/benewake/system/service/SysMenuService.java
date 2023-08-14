package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.system.SysMenu;
import com.benewake.system.entity.vo.AssginMenuVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author lcs
 * @since 2023-08-02 01:44:33
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单表实现
     * @return
     */
    List<SysMenu> findNodes(SysMenu sysMenu);

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    boolean removeMenuById(String id);

    /**
     * 根据角色id获取菜单信息
     * @param roleId
     * @return
     */
    List<SysMenu> findMenuByRoleId(String roleId);

    /**
     * 给角色分配菜单权限
     * @param assginMenuVo
     */
    void doAssign(AssginMenuVo assginMenuVo);

    /**
     * 根据用户id查询菜单和按钮权限
     *
     * @param id
     * @return
     */
    Map<String,Object> getUserMenuAndButtonList(String id);

}
