package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProcessScheme;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsProcessSchemeDto;
import com.benewake.system.entity.vo.ApsProcessSchemeVo;
import com.benewake.system.entity.vo.ProcessSchemeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_process_scheme】的数据库操作Mapper
* @createDate 2023-10-21 17:38:05
* @Entity com.benewake.system.entity.ApsProcessScheme
*/
@Mapper
public interface ApsProcessSchemeMapper extends BaseMapper<ApsProcessScheme> {

    List<String> selectSchemeBycaIdandNumber(@Param("processCapacityIds") List<Integer> processCapacityIds, @Param("number") Integer number);

    List<ApsProcessSchemeDto> selectProcessSchemeBycurrentProcessScheme(@Param("currentProcessScheme") String currentProcessScheme);

    List<ApsProcessScheme> selectListByIds(@Param("ids") List<Integer> ids);

    List<ProcessSchemeEntity> selectEmployeeTime(@Param("curProcessSchemeNameList") List<String> curProcessSchemeNameList);

    Page<ApsProcessSchemeDto> selectProcessSchemePage(Page<ApsProcessScheme> schemePage);

    List<ApsProcessSchemeDto> selectProcessSchemeByProcessScheme(@Param("currentProcessScheme") String currentProcessScheme, @Param("productFamily") String productFamily);

    List<ApsProcessSchemeVo> selectProcessScheme();
}




