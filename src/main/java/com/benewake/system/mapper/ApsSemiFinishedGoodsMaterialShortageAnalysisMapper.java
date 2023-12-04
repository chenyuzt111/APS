package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsMaterialShortageAnalysisDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_goods_material_shortage_analysis】的数据库操作Mapper
* @createDate 2023-11-02 11:39:24
* @Entity com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis
*/
@Mapper
public interface ApsSemiFinishedGoodsMaterialShortageAnalysisMapper extends BaseMapper<ApsSemiFinishedGoodsMaterialShortageAnalysis> {

    Page<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> selectPageList(Page<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> goodsProductionPlanPage, @Param("apsTableVersion") Integer apsTableVersion);
}




