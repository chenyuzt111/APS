package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsRawMaterialBasicData;
import com.benewake.system.entity.dto.ApsRawMaterialBasicDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_raw_material_basic_data】的数据库操作Mapper
 * @createDate 2023-12-04 09:27:44
 * @Entity com.benewake.system.entity.ApsRawMaterialBasicData
 */
@Mapper
public interface ApsRawMaterialBasicDataMapper extends BaseMapper<ApsRawMaterialBasicData> {

    Page<ApsRawMaterialBasicDataDto> selectListPage(Page<ApsRawMaterialBasicDataDto> materialBasicDataVoPage);

    Page<ApsRawMaterialBasicDataDto> selectPageLists(Page<Object> page, @Param(Constants.WRAPPER) QueryWrapper<Object> wrapper);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper);
}




