package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsPackagingTest;
import com.benewake.system.entity.dto.ApsPackagingTestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_packaging_test】的数据库操作Mapper
* @createDate 2023-10-19 14:44:22
* @Entity com.benewake.system.entity.ApsPackagingTest
*/
@Mapper
public interface ApsPackagingTestMapper extends BaseMapper<ApsPackagingTest> {

    Page<ApsPackagingTestDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




