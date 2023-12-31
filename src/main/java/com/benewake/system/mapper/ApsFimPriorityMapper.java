package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsFimPriority;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsFimPriorityDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_fim_priority】的数据库操作Mapper
 * @createDate 2023-11-06 09:59:19
 * @Entity com.benewake.system.entity.ApsFimPriority
 */
@Mapper
public interface ApsFimPriorityMapper extends BaseMapper<ApsFimPriority> {

    Page<ApsFimPriorityDto> selectPageList(Page<ApsFimPriorityDto> apsFimPriorityPage, @Param("apsTableVersion") Integer apsTableVersion);

    Page<Object> getFimPriorityFiltrate(Page<Object> page1,
                                        @Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                                        @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper,
                            @Param("versions") List<VersionToChVersion> versionToChVersionArrayList);
}




