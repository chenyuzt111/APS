package com.benewake.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.system.SysDept;

import java.util.List;

/**
 * <p>
 * 组织机构 服务类
 * </p>
 *
 * @author lcs
 * @since 2023-08-04 05:22:18
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 新增部门信息
     * @param dept
     * @return
     */
    int insertDept(SysDept dept);

    /**
     * 根据id删除部门信息
     * @param id
     * @return
     */
    boolean removeDeptById(String id);

    /**
     * 获取部门树形列表
     * @return
     */
    List<SysDept> findDepts();

    /**
     * 获取部门管理信息列表
     * @param sysDept
     * @return
     */
    List<SysDept> selectDeptList(SysDept sysDept);
}
