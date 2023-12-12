package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsSemiFinishedBasicData;
import com.benewake.system.entity.dto.ApsSemiFinishedBasicDataDto;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ASUS
* @description 针对表【aps_semi_finished_basic_data】的数据库操作Mapper
* @createDate 2023-12-04 13:35:18
* @Entity com.benewake.system.entity.ApsSemiFinishedBasicData
*/
@Mapper
public interface ApsSemiFinishedBasicDataMapper extends BaseMapper<ApsSemiFinishedBasicData> {

    Page<ApsSemiFinishedBasicDataDto> selectListPage(Page<ApsSemiFinishedBasicDataDto> dataDtoPage);
}




