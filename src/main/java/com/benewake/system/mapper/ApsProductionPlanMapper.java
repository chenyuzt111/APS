package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProductionPlan;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsProductionPlanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_production_plan】的数据库操作Mapper
 * @createDate 2023-10-23 16:40:42
 * @Entity com.benewake.system.entity.ApsProductionPlan
 */

@Mapper
public interface ApsProductionPlanMapper extends BaseMapper<ApsProductionPlan> {

    Page<ApsProductionPlanDto> selectPageList(Page<Object> apsProductionPlanPage, @Param("apsTableVersion") Integer apsTableVersion);

    Page<Object> queryPageList(Page<Object> apsProductionPlanPage,
                               @Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                               @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                            @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);
}




