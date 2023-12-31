package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsMaterialShortageAnalysis;
import com.benewake.system.entity.Interface.VersionToChVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_material_shortage_analysis】的数据库操作Mapper
 * @createDate 2023-11-02 11:39:08
 * @Entity com.benewake.system.entity.ApsMaterialShortageAnalysis
 */
@Mapper
public interface ApsMaterialShortageAnalysisMapper extends BaseMapper<ApsMaterialShortageAnalysis> {


    Page<Object> queryPageList(Page<Object> pageTemp,
                               @Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                               @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                            @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);
}




