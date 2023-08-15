package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lcs
 * @since 2023-08-01 06:36:31
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("<script>" +
            "select " +
            /*  数据列权限范围  */
            "${vo.param.colScope} " +
            "from sys_user u " +
            "left join sys_dept d on u.dept_id = d.id " +
            "where u.is_deleted = 0 and d.is_deleted = 0" +
            "<if test='vo.username != null and vo.username != &apos;&apos; '>" +
            "and u.username like CONCAT('%',#{vo.username},'%') " +
            "</if>" +
            "<if test='vo.deptId != null and vo.deptId != &apos;&apos; '>" +
            "and u.dept_id = #{vo.deptId} " +
            "</if>" +
            /*  数据行权限范围  */
            "${vo.param.dataScope} " +
            "order by u.id desc " +
            "</script>")
    List<Map<String,Object>> selectUser(@Param("vo") SysUser sysUser);

    @Select("<script>" +
            "select " +
            /*  数据列权限范围  */
            "${sysRole.param.colScope} " +
            "from sys_user u " +
            "left join sys_dept d on u.dept_id = d.id " +
            "left join sys_user_role ur on ur.user_id = u.id " +
            "where u.is_deleted = 0 and ur.is_deleted = 0 and d.is_deleted = 0 and ur.role_id = #{sysRole.id} " +
            /*  数据权限范围  */
            "${sysRole.param.dataScope} " +
            "order by u.id " +
            "</script>")
    List<Map<String,Object>> getUsersByRoleId(@Param("sysRole") SysRole sysRole);
}
