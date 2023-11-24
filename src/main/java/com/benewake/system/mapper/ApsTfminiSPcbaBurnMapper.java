package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSPcbaBurn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsTfminiSPcbaBurnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_pcba_burn】的数据库操作Mapper
* @createDate 2023-10-19 09:28:12
* @Entity com.benewake.system.entity.ApsTfminiSPcbaBurn
*/
@Mapper
public interface ApsTfminiSPcbaBurnMapper extends BaseMapper<ApsTfminiSPcbaBurn> {

    Page<ApsTfminiSPcbaBurnDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




