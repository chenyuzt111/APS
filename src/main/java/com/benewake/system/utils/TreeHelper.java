package com.benewake.system.utils;

import com.benewake.system.entity.system.SysDept;
import com.benewake.system.entity.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月02 14:07
 * 描 述： TOD
 */
public class TreeHelper {
    /**
     * 构建菜单树形结构
     * @param sysMenus
     * @return
     */
    public static List<SysMenu> bulidMenuTree(List<SysMenu> sysMenus) {
        // 创建集合封装最终数据
        List<SysMenu> trees = new ArrayList<>();
        // 遍历菜单集合
        sysMenus.forEach(s->{
            // 找到递归入口,parentid = 0
            if(s.getParentId().longValue()==0){
                trees.add(findChildren(s,sysMenus));
            }
        });
        return trees;
    }

    /**
     * 构建部门树形结构
     * @param sysDepts
     * @return
     */
    public static List<SysDept> bulidDeptTree(List<SysDept> sysDepts) {
        // 创建集合封装最终数据
        List<SysDept> trees = new ArrayList<>();
        // 遍历菜单集合
        sysDepts.forEach(s->{
            // 找到递归入口,parentid = 0
            if(s.getParentId().longValue()==0){
                trees.add(findChildren(s,sysDepts));
            }
        });
        return trees;
    }

    /**
     * 从根节点进行递归查询
     * 判断 id = parentid 相同则为子节点
     * @return
     */
    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> sysMenus) {
        sysMenu.setChildren(new ArrayList<>());
        // 递归查找
        sysMenus.forEach(s -> {
            long id = Long.parseLong(sysMenu.getId());
            long parentId = s.getParentId();
            // 相同则递归查找子节点
            if(id == parentId){
                sysMenu.getChildren().add(findChildren(s,sysMenus));
            }
        });
        return sysMenu;
    }

    /**
     * 从根节点进行递归查询
     * 判断 id = parentid 相同则为子节点
     * @return
     */
    private static SysDept findChildren(SysDept sysDept, List<SysDept> sysDepts) {
        sysDept.setChildren(new ArrayList<>());
        // 递归查找
        sysDepts.forEach(s -> {
            long id = Long.parseLong(sysDept.getId());
            long parentId = s.getParentId();
            // 相同则递归查找子节点
            if(id == parentId){
                sysDept.getChildren().add(findChildren(s,sysDepts));
            }
        });
        return sysDept;
    }
}
