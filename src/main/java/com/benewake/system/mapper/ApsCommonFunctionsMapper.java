package com.benewake.system.mapper;

import com.benewake.system.entity.ApsCommonFunctions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsCommonFunctionsDto;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
* @author 10451
* @description 针对表【aps_common_functions】的数据库操作Mapper
* @createDate 2024-01-03 10:39:45
* @Entity com.benewake.system.entity.ApsCommonFunctions
*/
public interface ApsCommonFunctionsMapper extends BaseMapper<ApsCommonFunctions> {

     List<ApsCommonFunctions> getCommonFunctionsByUserId(@Param("userId") int userId);

    void deleteCommonFunctionsByUserId(int userId);
    void insertCommonFunctions(List<ApsCommonFunctions> apsCommonFunctions);

}




