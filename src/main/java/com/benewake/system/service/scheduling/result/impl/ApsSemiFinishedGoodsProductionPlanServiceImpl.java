package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.*;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsSemiFinishedGoodsProductionPlanDto;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsSemiFinishedGoodsProductionPlanMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsSemiFinishedGoodsProductionPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_semi_finished_goods_production_plan】的数据库操作Service实现
 * @createDate 2023-11-02 11:38:43
 */
@Slf4j
@Service
public class ApsSemiFinishedGoodsProductionPlanServiceImpl extends ServiceImpl<ApsSemiFinishedGoodsProductionPlanMapper, ApsSemiFinishedGoodsProductionPlan>
        implements ApsSemiFinishedGoodsProductionPlanService {


    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsSemiFinishedGoodsProductionPlanMapper semiFGoodProductionPlanMapper;


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
    public PageResultVo<ApsSemiFinishedGoodsProductionPlanDto> getAllPage(Integer page, Integer size) {
//        Integer apsTableVersion = getApsTableVersion(SchedulingResultType.APS_SEMI_FINISHED_GOODS_PRODUCTION_PLAN.getCode(), apsTableVersionService);
        Page<ApsSemiFinishedGoodsProductionPlan> goodsProductionPlanPage = new Page<>();
        goodsProductionPlanPage.setSize(size);
        goodsProductionPlanPage.setCurrent(page);
        PageResultVo<ApsSemiFinishedGoodsProductionPlanDto> restVo = new PageResultVo<>();
        return restVo;
    }

    @Override
    public void defaultSort(QueryWrapper queryWrapper) {
        String customSqlSegment = queryWrapper.getCustomSqlSegment();
        if (!customSqlSegment.contains("ORDER")) {
            queryWrapper.orderByDesc("ch_version_name");
            queryWrapper.orderByAsc("f_start_time");
            queryWrapper.orderByAsc("f_material_code");
        }
    }

    @Override
    public ResultColPageVo<Object> getResultFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        return commonFiltrate(page, size, queryViewParams,
                (objectPage, objectQueryWrapper, versionToChVersionArrayList) ->
                        semiFGoodProductionPlanMapper.queryPageList(objectPage, objectQueryWrapper, versionToChVersionArrayList));
    }


    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper, List<VersionToChVersion> versionToChVersionArrayList) {
        return semiFGoodProductionPlanMapper.searchLike(queryWrapper, versionToChVersionArrayList);
    }
}





