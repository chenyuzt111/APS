package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsCalibrationTests;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsCalibrationTestsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_calibration_tests】的数据库操作Mapper
* @createDate 2023-10-19 14:19:43
* @Entity com.benewake.system.entity.ApsCalibrationTests
*/
@Mapper
public interface ApsCalibrationTestsMapper extends BaseMapper<ApsCalibrationTests> {

    Page<ApsCalibrationTestsDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




