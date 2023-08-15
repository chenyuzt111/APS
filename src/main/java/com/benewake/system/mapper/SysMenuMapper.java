package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author lcs
 * @since 2023-08-02 01:44:33
 */
@Mapper
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据id查找用户菜单权限
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select distinct (m.id),m.parent_id ,m.name ,m.`type` ,m.`path` ," +
            "m.component ,m.perms ,m.icon,m.sort_value ,m.create_time ,m.update_time ,m.is_deleted " +
            "from sys_menu m left join sys_role_menu rm on m.id = rm.menu_id " +
            "left join sys_user_role ur on rm.role_id = ur.role_id " +
            "where ur.user_id = #{userId} and rm.is_deleted = 0 and ur.is_deleted = 0 and m.is_deleted = 0 " +
            "</script>")
    List<SysMenu> findMenuListByUserId(@Param("userId") String userId);

}
