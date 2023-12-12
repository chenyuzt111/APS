package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsPcbaBurn;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsPcbaBurnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_pcba_burn】的数据库操作Mapper
* @createDate 2023-10-18 16:38:43
* @Entity com.benewake.system.entity.ApsPcbaBurn
*/
@Mapper
public interface ApsPcbaBurnMapper extends BaseMapper<ApsPcbaBurn> {

    Page<ApsPcbaBurnDto> selectPageList(Page objectPage,
                                        @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




