package com.benewake.system.service.scheduling.result;

import com.benewake.system.entity.ApsFimPriority;
import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.dto.ApsFimPriorityDto;
import com.benewake.system.entity.vo.PageListRestVo;

/**
* @author ASUS
* @description 针对表【aps_fim_priority】的数据库操作Service
* @createDate 2023-11-06 09:59:19
*/
public interface ApsFimPriorityService extends IService<ApsFimPriority> ,ApsSchedulingResuleBase{

    PageListRestVo<ApsFimPriorityDto> getAllPage(Integer page, Integer size);
}
