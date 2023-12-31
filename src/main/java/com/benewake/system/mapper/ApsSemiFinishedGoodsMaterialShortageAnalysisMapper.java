package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsMaterialShortageAnalysisDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_semi_finished_goods_material_shortage_analysis】的数据库操作Mapper
 * @createDate 2023-11-02 11:39:24
 * @Entity com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis
 */
@Mapper
public interface ApsSemiFinishedGoodsMaterialShortageAnalysisMapper extends BaseMapper<ApsSemiFinishedGoodsMaterialShortageAnalysis> {

    Page<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> selectPageList(Page<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> goodsProductionPlanPage, @Param("apsTableVersion") Integer apsTableVersion);

    Page<Object> queryPageList(Page<Object> pageTemp,
                               @Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                               @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                            @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);
}




