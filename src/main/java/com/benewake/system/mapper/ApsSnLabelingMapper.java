package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsSnLabeling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsSnLabelingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_sn_labeling】的数据库操作Mapper
* @createDate 2023-10-19 13:50:27
* @Entity com.benewake.system.entity.ApsSnLabeling
*/
@Mapper
public interface ApsSnLabelingMapper extends BaseMapper<ApsSnLabeling> {

    Page<ApsSnLabelingDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




