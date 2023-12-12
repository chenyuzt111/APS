package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsMesTotal;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsMesTotalDto;
import com.benewake.system.entity.dto.ApsPcbaBurnDto;
import com.benewake.system.entity.mes.MesPcbaBurn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author DELL
* @description 针对表【aps_mes_total】的数据库操作Mapper
* @createDate 2023-12-11 13:52:23
* @Entity com.benewake.system.entity.mes.MesPcbaBurn.ApsMesTotal
*/
@Mapper
public interface ApsMesTotalMapper extends BaseMapper<ApsMesTotal> {
    Page<ApsMesTotalDto> selectPageList(Page objectPage,
                                        @Param("versions") List<VersionToChVersion> versions);

    void insertVersionIncr();
}




