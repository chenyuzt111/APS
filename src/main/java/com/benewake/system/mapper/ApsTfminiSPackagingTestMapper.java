package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSPackagingTest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsTfminiSPackagingTestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_packaging_test】的数据库操作Mapper
* @createDate 2023-10-19 14:52:15
* @Entity com.benewake.system.entity.ApsTfminiSPackagingTest
*/
@Mapper
public interface ApsTfminiSPackagingTestMapper extends BaseMapper<ApsTfminiSPackagingTest> {

    Page<ApsTfminiSPackagingTestDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




