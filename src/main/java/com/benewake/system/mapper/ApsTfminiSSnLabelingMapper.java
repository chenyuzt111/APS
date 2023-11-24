package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSSnLabeling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsTfminiSSnLabelingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_sn_labeling】的数据库操作Mapper
* @createDate 2023-10-19 13:59:51
* @Entity com.benewake.system.entity.ApsTfminiSSnLabeling
*/
@Mapper
public interface ApsTfminiSSnLabelingMapper extends BaseMapper<ApsTfminiSSnLabeling> {

    Page<ApsTfminiSSnLabelingDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




