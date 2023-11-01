package com.benewake.system.service;

import com.benewake.system.entity.ApsProcessNamePool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.vo.ApsProcessNamePoolVo;

/**
* @author ASUS
* @description 针对表【aps_process_name_pool】的数据库操作Service
* @createDate 2023-10-20 09:20:16
*/
public interface ApsProcessNamePoolService extends IService<ApsProcessNamePool> {

    Boolean addOrUpdateProcess(ApsProcessNamePool apsProcessNamePool);

    ApsProcessNamePoolVo getProcess(String name, Integer page, Integer size);
}
