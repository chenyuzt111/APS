package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.SysRoleMenu;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lcs
 */
@Mapper
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    @Insert("<script>" +
            "insert into sys_role_menu(menu_id,role_id) " +
            "values " +
            "<foreach collection='newList' item='t' separator=','> " +
            "(#{t.menuId},#{t.roleId})" +
            "</foreach>" +
            "</script>")
    void insertRoleMenus(@Param("newList") List<SysRoleMenu> sysRoleMenus);
}
