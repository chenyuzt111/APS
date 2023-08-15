package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.ApsCol;
import com.benewake.system.entity.system.SysRole;
import com.benewake.system.entity.system.SysUserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月02 10:50
 * 描 述： TODO
 */
@Mapper
@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    /**
     * 批量保存用户角色表信息
     * @param newList
     * @return
     */
    @Insert("<script>" +
            "insert into sys_user_role(role_id,user_id) " +
            "values " +
            "<foreach collection='newList' item='t' separator=','> " +
            "(#{t.roleId},#{t.userId})" +
            "</foreach>" +
            "</script>")
    Integer saveList(@Param("newList") List<SysUserRole> newList);

    /**
     * 查询用户所属角色表信息
     * @param id
     * @return
     */
    @Select("<script>" +
            "select id,role_name,role_code,description,data_scope " +
            "from sys_role where id in (" +
            "select role_id from sys_user_role " +
            "where user_id = #{id} and is_deleted = 0 " +
            ") and is_deleted = 0 " +
            "</script>")
    List<SysRole> getUserRoles(@Param("id") String id);

}
