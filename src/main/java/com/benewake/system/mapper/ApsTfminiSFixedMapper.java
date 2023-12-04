package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSFixed;
import com.benewake.system.entity.dto.ApsTfminiSFixedDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* @author DELL
* @description 针对表【aps_tfmini_s_fixed】的数据库操作Mapper
* @createDate 2023-11-27 14:35:29
* @Entity generator.domain.ApsTfminiSFixed
*/
@Mapper
public interface ApsTfminiSFixedMapper extends BaseMapper<ApsTfminiSFixed> {

    Page<ApsTfminiSFixedDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




