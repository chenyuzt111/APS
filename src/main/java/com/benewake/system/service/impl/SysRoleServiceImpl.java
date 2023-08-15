package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.system.ApsCol;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUserRole;
import com.benewake.system.entity.vo.AssginRoleVo;
import com.benewake.system.entity.vo.SysRoleQueryVo;
import com.benewake.system.mapper.SysRoleMapper;
import com.benewake.system.mapper.SysUserRoleMapper;
import com.benewake.system.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lcs
 * @since 2023年07月29 16:38
 * 描 述： TODO
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysRole> selectRole(SysRoleQueryVo sysRoleQueryVo) {
        LambdaQueryWrapper<SysRole> lqw = new LambdaQueryWrapper<>();
        lqw.select(SysRole::getId,SysRole::getRoleName,SysRole::getRoleCode,SysRole::getDescription,
                SysRole::getCreateTime,SysRole::getUpdateTime,SysRole::getIsDeleted)
                .like(StringUtils.isNotBlank(sysRoleQueryVo.getRoleName()),SysRole::getRoleName,sysRoleQueryVo.getRoleName())
                .eq(SysRole::getIsDeleted,0)
                .orderByDesc(SysRole::getId);
        return baseMapper.selectList(lqw);
    }

    @Override
    public boolean isExist(SysRole sysRole) {
        LambdaQueryWrapper<SysRole> lqw = new LambdaQueryWrapper<>();
        lqw.select(SysRole::getId)
                .eq(SysRole::getRoleName,sysRole.getRoleName());
        return baseMapper.selectList(lqw).size() > 0;
    }

    @Override
    public Map<String, Object> getRolesByUserId(String userId) {
        Map<String,Object> map = new HashMap<>();
        // 获取所有角色
        // 可以使用redis缓存所有角色（后期）
        List<SysRole> roles = baseMapper.selectList(null);
        // 获取用户已分配的角色
        LambdaQueryWrapper<SysUserRole> lqw = new LambdaQueryWrapper<>();
        lqw.select(SysUserRole::getRoleId)
                .eq(SysUserRole::getUserId,userId);
        List<SysUserRole> userRoleIds = sysUserRoleMapper.selectList(lqw);
        map.put("allRoles",roles);
        map.put("userRoleIds",userRoleIds);
        return map;
    }

    @Override
    public Integer doAssign(AssginRoleVo assginRoleVo) {
        // 根据用户id删除之前分配的角色
        LambdaQueryWrapper<SysUserRole> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysUserRole::getUserId,assginRoleVo.getUserId());
        sysUserRoleMapper.delete(lqw);
        // 获取所有角色id，添加角色用户关系表
        List<SysUserRole> newList = new ArrayList<>();
        assginRoleVo.getRoleIdList().forEach(r->{
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assginRoleVo.getUserId());
            sysUserRole.setRoleId(r);
            newList.add(sysUserRole);
        });
        return sysUserRoleMapper.saveList(newList);
    }

    @Override
    public List<SysRole> getUserRoles(String id) {
        return sysUserRoleMapper.getUserRoles(id);
    }

}
