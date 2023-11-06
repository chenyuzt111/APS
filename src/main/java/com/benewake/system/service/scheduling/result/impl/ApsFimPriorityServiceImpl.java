package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsFimPriority;
import com.benewake.system.entity.vo.PageListRestVo;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsFimPriorityService;
import com.benewake.system.mapper.ApsFimPriorityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.benewake.system.entity.enums.SchedulingResultType.APS_FIM_PRIORITY;

/**
* @author ASUS
* @description 针对表【aps_fim_priority】的数据库操作Service实现
* @createDate 2023-11-06 09:59:19
*/
@Service
public class ApsFimPriorityServiceImpl extends ServiceImpl<ApsFimPriorityMapper, ApsFimPriority>
    implements ApsFimPriorityService{

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Override
    public PageListRestVo<ApsFimPriority> getAllPage(Integer page, Integer size) {
        Integer apsTableVersion = this.getApsTableVersion(APS_FIM_PRIORITY.getCode(), apsTableVersionService);
        LambdaQueryWrapper<ApsFimPriority> apsFimPriorityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsFimPriorityLambdaQueryWrapper.eq(ApsFimPriority::getVersion ,apsTableVersion);
        Page<ApsFimPriority> apsFimPriorityPage = new Page<>();
        apsFimPriorityPage.setCurrent(page);
        apsFimPriorityPage.setSize(size);
        Page<ApsFimPriority> priorityPage = page(apsFimPriorityPage, apsFimPriorityLambdaQueryWrapper);
        PageListRestVo<ApsFimPriority> apsFimPriorityPageListRestVo = new PageListRestVo<>();
        apsFimPriorityPageListRestVo.setList(priorityPage.getRecords());
        apsFimPriorityPageListRestVo.setPages(priorityPage.getPages());
        apsFimPriorityPageListRestVo.setSize(size);
        apsFimPriorityPageListRestVo.setPage(page);
        apsFimPriorityPageListRestVo.setTotal(priorityPage.getTotal());
        return apsFimPriorityPageListRestVo;
    }
}




