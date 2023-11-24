package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSCalibrationTests;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsTfminiSCalibrationTestsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_calibration_tests】的数据库操作Mapper
* @createDate 2023-10-19 14:29:24
* @Entity com.benewake.system.entity.ApsTfminiSCalibrationTests
*/
@Mapper
public interface ApsTfminiSCalibrationTestsMapper extends BaseMapper<ApsTfminiSCalibrationTests> {

    Page<ApsTfminiSCalibrationTestsDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




