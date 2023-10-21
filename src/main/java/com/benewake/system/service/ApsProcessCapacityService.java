package com.benewake.system.service;

import com.benewake.system.entity.ApsProcessCapacity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;

/**
* @author ASUS
* @description 针对表【aps_process_capacity(工序与产能表)】的数据库操作Service
* @createDate 2023-10-20 15:03:52
*/
public interface ApsProcessCapacityService extends IService<ApsProcessCapacity> {

    Boolean saveOrUpdateProcessCapacityService(ApsProcessCapacityVo apsProcessCapacityVo);

    ApsProcessCapacityListVo getAllProcessCapacity(Integer page, Integer size);
}
