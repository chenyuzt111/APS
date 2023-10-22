package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.system.ApsCol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Lcs
 * @since 2023年08月14 16:19
 * 描 述： TOD
 */
@Mapper
public interface ApsColMapper extends BaseMapper<ApsCol> {
    @Select("<script>" +
            "select act.col_id,act.menu_id,act.col_name_ENG,act.col_name_CN,act.col_name " +
            "from aps_col_table act " +
            "where act.menu_id = #{menuId} " +
            "</script>")
    List<ApsCol> getColsByMenuId(@Param("menuId") String menuId);

    @Select("<script>" +
            "select act.col_name " +
            "from aps_col_table act " +
            "left join aps_role_col arc on act.col_id = arc.col_id " +
            "where arc.role_id = #{roleId} and arc.menu_id = #{menuId} " +
            "</script>")
    List<ApsCol> getColsByRoleAndMenu(@Param("roleId") String roleId, @Param("menuId") String menuId);
}
