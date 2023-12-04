package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsMaterialShortageAnalysis;
import com.benewake.system.entity.dto.ApsMaterialShortageAnalysisDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author ASUS
* @description 针对表【aps_material_shortage_analysis】的数据库操作Mapper
* @createDate 2023-11-02 11:39:08
* @Entity com.benewake.system.entity.ApsMaterialShortageAnalysis
*/
@Mapper
public interface ApsMaterialShortageAnalysisMapper extends BaseMapper<ApsMaterialShortageAnalysis> {

    Page<ApsMaterialShortageAnalysisDto> selectPageList(Page<ApsMaterialShortageAnalysisDto> pageTemp, @Param("apsTableVersion") Integer apsTableVersion);
}




