package com.benewake.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.SysDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 组织机构 Mapper 接口
 * </p>
 *
 * @author lcs
 * @since 2023-08-04 05:22:18
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    @Select("<script>" +
            "select " +
            "${dept.param.colScope} " +
            "from sys_dept d " +
            "where d.is_deleted = 0 " +
            "<if test='dept.name!=null'>" +
            " and d.name like CONCAT('%',#{dept.name},'%') " +
            "</if>" +
            "<if test='dept.leader!=null'>" +
            " and d.leader like CONCAT('%',#{dept.leader},'%') " +
            "</if>" +
            "<if test='dept.id!=null'>" +
            " and d.id = #{dept.id} " +
            "</if>" +
            "<if test='dept.parentId!=null'>" +
            " and d.parent_id = #{dept.parentId} " +
            "</if>" +
            " ${dept.param.dataScope} " +
            "order by d.id" +
            "</script>")
    List<SysDept> selectDeptList(@Param("dept") SysDept sysDept);
}
