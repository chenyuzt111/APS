package com.benewake.system.service;

import com.benewake.system.entity.system.ApsCol;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月14 16:16
 * 描 述： TODO
 */
public interface ApsColService {

    /**
     * 根据menuId获取列信息
     * @param menuId
     * @return
     */
    List<ApsCol> getColsByMenuId(String menuId);

    /**
     * 根据角色id和菜单id获取角色列权限
     *
     * @param id
     * @param menuId
     * @return
     */
    List<ApsCol> getColsByRoleAndMenu(String id, String menuId);
}
