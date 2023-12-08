package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.*;
import com.benewake.system.entity.dto.ApsProductionPlanDto;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsProductionPlanMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsProductionPlanService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_production_plan】的数据库操作Service实现
 * @createDate 2023-10-23 16:40:42
 */
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
    public ResultColPageVo<Object> getProductionFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        return commonFiltrate(page, size, SchedulingResultType.APS_PRODUCTION_PLAN, queryViewParams,
                (objectPage, objectQueryWrapper) ->
                        apsProductionPlanMapper.queryPageList(objectPage, objectQueryWrapper));
    }


}