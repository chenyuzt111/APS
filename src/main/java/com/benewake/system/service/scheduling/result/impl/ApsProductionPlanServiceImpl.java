package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.*;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsProductionPlanMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsProductionPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_production_plan】的数据库操作Service实现
 * @createDate 2023-10-23 16:40:42
 */
@Slf4j
@Service
public class ApsProductionPlanServiceImpl extends ServiceImpl<ApsProductionPlanMapper, ApsProductionPlan>
        implements ApsProductionPlanService {
    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsProductionPlanMapper apsProductionPlanMapper;

    @Autowired
    private ApsViewColTableMapper viewColTableMapper;

    @Autowired
    private ApsColumnTableMapper columnTableMapper;

    @Override
    public ApsTableVersionService getTableVersionService() {
        return apsTableVersionService;
    }

    @Override
    public ApsViewColTableMapper getViewColTableMapper() {
        return viewColTableMapper;
    }

    @Override
    public ApsColumnTableMapper getColumnTableMapper() {
        return columnTableMapper;
    }


    @Override
    public ResultColPageVo<Object> getResultFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        return commonFiltrate(page, size, SchedulingResultType.APS_PRODUCTION_PLAN, queryViewParams,
                (objectPage, objectQueryWrapper ,versionToChVersionArrayList) ->
                        apsProductionPlanMapper.queryPageList(objectPage, objectQueryWrapper ,versionToChVersionArrayList));
    }

    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper, List<VersionToChVersion> versionToChVersionArrayList) {
        return apsProductionPlanMapper.searchLike(queryWrapper,versionToChVersionArrayList);
    }
}