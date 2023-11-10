package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProductionPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsProductionPlanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author ASUS
* @description 针对表【aps_production_plan】的数据库操作Mapper
* @createDate 2023-10-23 16:40:42
* @Entity com.benewake.system.entity.ApsProductionPlan
*/

@Mapper
public interface ApsProductionPlanMapper extends BaseMapper<ApsProductionPlan> {

    Page<ApsProductionPlanDto> selectPageList(Page<ApsProductionPlan> apsProductionPlanPage, @Param("apsTableVersion") Integer apsTableVersion);
}




