package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.system.SysMenu;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysRoleMenu;
import com.benewake.system.entity.vo.AssginMenuVo;
import com.benewake.system.entity.vo.RouterVo;
import com.benewake.system.mapper.SysMenuMapper;
import com.benewake.system.mapper.SysRoleMenuMapper;
import com.benewake.system.service.SysMenuService;
import com.benewake.system.utils.RouterHelper;
import com.benewake.system.utils.TreeHelper;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author lcs
 * @since 2023-08-02 01:44:33
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Override
    public List<SysMenu> findNodes(SysMenu sysMenu) {
        List<SysMenu> list = new ArrayList<>();
        LambdaQueryWrapper<SysMenu> lqw = new LambdaQueryWrapper<>();
        // lqw.like(StringUtils.isNotBlank(sysMenu.getName()),SysMenu::getName,sysMenu.getName());
        list = baseMapper.selectList(lqw);
        // 格式转换
        return TreeHelper.bulidMenuTree(list);
    }

    @Override
    public boolean removeMenuById(String id) {
        // 查询当前菜单下面是否有子菜单
        LambdaQueryWrapper<SysMenu> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysMenu::getParentId,id);
        Long count = baseMapper.selectCount(lqw);
        if(count==0){
            baseMapper.deleteById(id);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<SysMenu> findMenuByRoleId(SysRole sysRole) {
        // 获取所有菜单 state = 1
        val menuList = baseMapper.selectList(null);
        // 根据角色id查询角色分配过的菜单信息
        LambdaQueryWrapper<SysRoleMenu> lqwSRM = new LambdaQueryWrapper<>();
        lqwSRM.eq(SysRoleMenu::getRoleId, sysRole.getId());
        val sysRoleMenus = sysRoleMenuMapper.selectList(lqwSRM);
        // 设置isSelect
        Set<String> set = new HashSet<>();
        sysRoleMenus.forEach(s->set.add(s.getMenuId()));
        menuList.forEach(s->{
            if(set.contains(s.getId())){
                s.setSelect(true);
            }else{
                s.setSelect(false);
            }
        });
        return TreeHelper.bulidMenuTree(menuList);
    }

    @Override
    public void doAssign(AssginMenuVo assginMenuVo) {
        // 根据角色id删除已有菜单
        LambdaQueryWrapper<SysRoleMenu> srmLqw = new LambdaQueryWrapper<>();
        srmLqw.eq(SysRoleMenu::getRoleId,assginMenuVo.getRoleId());
        sysRoleMenuMapper.delete(srmLqw);
        // 遍历菜单id列表并添加
        List<String> menuIdList = assginMenuVo.getMenuIdList();
        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        menuIdList.forEach(m->{
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(m);
            sysRoleMenu.setRoleId(assginMenuVo.getRoleId());
            sysRoleMenus.add(sysRoleMenu);
        });
        if(sysRoleMenus.size()==0){
            return;
        }
        sysRoleMenuMapper.insertRoleMenus(sysRoleMenus);

    }

    @Override
    public Map<String, Object> getUserMenuAndButtonList(String userId) {
        List<SysMenu> sysMenuList = null;
        if("1".equals(userId)){
            // 用户id值为1时表示超级管理员 可以操作所有内容 即有所有权限
            LambdaQueryWrapper<SysMenu> lqw = new LambdaQueryWrapper<>();
            lqw.orderByDesc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(lqw);
        }else {
            // 用户id不为1，表示其他类型用户,查询其相应的权限
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        // 获取按钮权限
        List<String> permsList = new ArrayList<>();
        sysMenuList.forEach(s->{
            if(s.getType() == 2){
                permsList.add(s.getPerms());
            }
        });

        // 获取菜单权限
        // 构建树形结构
        List<SysMenu> sysMenuTreeList = TreeHelper.bulidMenuTree(sysMenuList);
        // 转换成前端路由要求格式数据
        List<RouterVo> routerVoList = RouterHelper.buildRouters(sysMenuTreeList);

        Map<String,Object> map = new HashMap<>();
        map.put("permsList",permsList);
        map.put("routerVoList",routerVoList);
        return map;
    }
}
