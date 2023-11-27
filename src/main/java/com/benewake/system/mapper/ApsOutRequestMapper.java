package com.benewake.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsOutRequest;
import com.benewake.system.entity.dto.ApsOutRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author DELL
 * @description 针对表【aps_out_request】的数据库操作Mapper
 * @createDate 2023-11-13 10:57:34
 * @Entity generator.domain.ApsOutRequest
 */
@Mapper
public interface ApsOutRequestMapper extends BaseMapper<ApsOutRequest> {

    void insertVersionIncr();

    Page<ApsOutRequestDto> selectPageList(Page page, @Param("versions") List tableVersionList);
}
