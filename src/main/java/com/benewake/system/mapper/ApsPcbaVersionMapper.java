package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsPcbaVersion;
import com.benewake.system.entity.dto.ApsPcbaVersionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_pcba_version】的数据库操作Mapper
* @createDate 2023-10-19 10:16:37
* @Entity com.benewake.system.entity.ApsPcbaVersion
*/
@Mapper
public interface ApsPcbaVersionMapper extends BaseMapper<ApsPcbaVersion> {

    Page<ApsPcbaVersionDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




