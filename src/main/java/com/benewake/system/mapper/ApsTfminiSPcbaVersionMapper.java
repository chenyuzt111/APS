package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSPcbaVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsTfminiSPcbaVersionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_pcba_version】的数据库操作Mapper
* @createDate 2023-10-19 10:34:24
* @Entity com.benewake.system.entity.ApsTfminiSPcbaVersion
*/
@Mapper
public interface ApsTfminiSPcbaVersionMapper extends BaseMapper<ApsTfminiSPcbaVersion> {

    Page<ApsTfminiSPcbaVersionDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




