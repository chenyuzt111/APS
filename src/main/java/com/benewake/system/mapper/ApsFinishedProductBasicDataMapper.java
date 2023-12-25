package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.dto.ApsFinishedProductBasicDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_finished_product_basic_data】的数据库操作Mapper
 * @createDate 2023-10-20 11:27:46
 * @Entity com.benewake.system.entity.ApsFinishedProductBasicData
 */
@Mapper
public interface ApsFinishedProductBasicDataMapper extends BaseMapper<ApsFinishedProductBasicData> {

    Page<ApsFinishedProductBasicDataDto> selectListPage(Page<ApsFinishedProductBasicDataDto> productBasicDataDtoPage);

    Page<ApsFinishedProductBasicDataDto> selectPageLists(Page<Object> page, @Param(Constants.WRAPPER) QueryWrapper<Object> wrapper);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper);
}




