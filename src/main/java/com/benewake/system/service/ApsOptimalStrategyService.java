package com.benewake.system.service;

import com.benewake.system.entity.ApsOptimalStrategy;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.UpdateOptimalStrategyParam;

/**
* @author ASUS
* @description 针对表【aps_optimal_strategy】的数据库操作Service
* @createDate 2023-10-24 21:02:26
*/
public interface ApsOptimalStrategyService extends IService<ApsOptimalStrategy> {

    void ifInsert(ApsOptimalStrategy apsOptimalStrategy);

    Boolean updateAndOptimalProcess(UpdateOptimalStrategyParam updateOptimalStrategyParam);
}
