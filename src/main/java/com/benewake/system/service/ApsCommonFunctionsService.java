package com.benewake.system.service;

import com.benewake.system.entity.ApsCommonFunctions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ApsCommonFunctionsVo;

import java.util.List;

/**
* @author 10451
* @description 针对表【aps_common_functions】的数据库操作Service
* @createDate 2024-01-03 10:39:46
*/
public interface ApsCommonFunctionsService extends IService<ApsCommonFunctions> {

    List<ApsCommonFunctionsVo> getCommonFunctionsByUserId(Integer userId);

    void updateCommonFunctions(List<ApsCommonFunctionsVo> apsCommonFunctionVos, int userId);
}
