package com.benewake.system.service.scheduling.result;

import com.baomidou.mybatisplus.extension.service.IService;
import com.benewake.system.entity.ApsFimPriority;
import com.benewake.system.entity.dto.ApsFimPriorityDto;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;

import javax.servlet.http.HttpServletResponse;

/**
* @author ASUS
* @description 针对表【aps_fim_priority】的数据库操作Service
* @createDate 2023-11-06 09:59:19
*/
public interface ApsFimPriorityService extends IService<ApsFimPriority> ,ApsSchedulingResuleBase{

    PageResultVo<ApsFimPriorityDto> getAllPage(Integer page, Integer size);

    void downloadFimRequest(HttpServletResponse response, DownloadParam downloadParam);

    ResultColPageVo<Object> getFimPriorityFiltrate(Integer page, Integer size, QueryViewParams queryViewParams);
}
