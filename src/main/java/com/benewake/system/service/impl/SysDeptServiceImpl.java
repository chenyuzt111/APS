package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.annotation.DataScope;
import com.benewake.system.entity.system.SysDept;
import com.benewake.system.mapper.SysDeptMapper;
import com.benewake.system.service.SysDeptService;
import com.benewake.system.utils.TreeHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织机构 服务实现类
 * </p>
 *
 * @author lcs
 * @since 2023-08-04 05:22:18
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {


    @Override
    public int insertDept(SysDept dept) {
        if(dept.getParentId() == null){
            dept.setParentId(0L);
        }
        SysDept info = baseMapper.selectById(dept.getParentId());

        dept.setAncestors(info.getAncestors());
        baseMapper.insert(dept);
        // 更新祖级列表
        dept.setAncestors(dept.getAncestors()+","+dept.getId());
        LambdaUpdateWrapper<SysDept> luw = new LambdaUpdateWrapper<>();
        luw.set(SysDept::getAncestors,dept.getAncestors()).eq(SysDept::getId,dept.getId());
        return baseMapper.update(null,luw);
    }

    @Override
    public boolean removeDeptById(String id) {
        // 判断是否还存在子部门
        LambdaQueryWrapper<SysDept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDept::getParentId,id);
        Long count = baseMapper.selectCount(queryWrapper);
        if(count == 0){
            // 不存在子部门 逻辑删除
            baseMapper.deleteById(queryWrapper);
            return true;
        }else{
            // 存在子部门 无法删除
            return false;
        }
    }

    @Override
    public List<SysDept> findDepts() {
        // 获取所有部门名称和id信息
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.select(SysDept::getId,SysDept::getName,SysDept::getParentId,SysDept::getSortValue);
        List<SysDept> list = baseMapper.selectList(lqw);
        if(list.size()==0) {
            return new ArrayList<>();
        }
        return TreeHelper.bulidDeptTree(list);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept sysDept) {
        return baseMapper.selectDeptList(sysDept);
    }
}
