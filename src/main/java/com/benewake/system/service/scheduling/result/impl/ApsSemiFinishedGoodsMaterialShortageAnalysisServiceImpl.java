package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsSemiFinishedGoodsMaterialShortageAnalysis;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsMaterialShortageAnalysisDto;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsSemiFinishedGoodsMaterialShortageAnalysisMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsSemiFinishedGoodsMaterialShortageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_semi_finished_goods_material_shortage_analysis】的数据库操作Service实现
 * @createDate 2023-11-02 11:39:24
 */
@Service
public class ApsSemiFinishedGoodsMaterialShortageAnalysisServiceImpl extends ServiceImpl<ApsSemiFinishedGoodsMaterialShortageAnalysisMapper, ApsSemiFinishedGoodsMaterialShortageAnalysis>
        implements ApsSemiFinishedGoodsMaterialShortageAnalysisService {

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsSemiFinishedGoodsMaterialShortageAnalysisMapper semiFinishedGoodsMaterialShortageAnalysisMapper;

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
    public PageResultVo<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> getAllPage(Integer page, Integer size) {
        Integer apsTableVersion = getApsTableVersion(SchedulingResultType.APS_SEMI_FINISHED_GOODS_MATERIAL_SHORTAGE_ANALYSIS.getCode(), apsTableVersionService);
        Page<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> goodsProductionPlanPage = new Page<>();
        goodsProductionPlanPage.setSize(size);
        goodsProductionPlanPage.setCurrent(page);
        Page<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> productionPlanPage = semiFinishedGoodsMaterialShortageAnalysisMapper.selectPageList(goodsProductionPlanPage, apsTableVersion);
        PageResultVo<ApsSemiFinishedGoodsMaterialShortageAnalysisDto> restVo = new PageResultVo<>();
        restVo.setList(productionPlanPage.getRecords());
        restVo.setPages(productionPlanPage.getPages());
        restVo.setTotal(productionPlanPage.getTotal());
        restVo.setPage(page);
        restVo.setSize(size);
        return restVo;
    }

    @Override
    public ResultColPageVo<Object> getResultFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        return commonFiltrate(page, size, SchedulingResultType.APS_SEMI_FINISHED_GOODS_MATERIAL_SHORTAGE_ANALYSIS, queryViewParams
                , (pageTemp, queryWrapper) -> semiFinishedGoodsMaterialShortageAnalysisMapper.queryPageList(pageTemp, queryWrapper));
    }

    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper) {
        Integer version = getApsTableVersion(SchedulingResultType.APS_SEMI_FINISHED_GOODS_MATERIAL_SHORTAGE_ANALYSIS.getCode(), apsTableVersionService);
        queryWrapper.eq("version" ,version);
        return semiFinishedGoodsMaterialShortageAnalysisMapper.searchLike(queryWrapper);
    }
}




