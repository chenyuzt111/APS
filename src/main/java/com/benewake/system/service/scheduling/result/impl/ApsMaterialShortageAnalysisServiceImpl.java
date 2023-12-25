package com.benewake.system.service.scheduling.result.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsMaterialShortageAnalysis;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsMaterialShortageAnalysisDto;
import com.benewake.system.entity.enums.SchedulingResultType;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsMaterialShortageAnalysisMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsMaterialShortageAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_material_shortage_analysis】的数据库操作Service实现
 * @createDate 2023-11-02 11:39:08
 */
@Service
public class ApsMaterialShortageAnalysisServiceImpl extends ServiceImpl<ApsMaterialShortageAnalysisMapper, ApsMaterialShortageAnalysis>
        implements ApsMaterialShortageAnalysisService {

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsMaterialShortageAnalysisMapper materialShortageAnalysisMapper;

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
    public PageResultVo<ApsMaterialShortageAnalysisDto> getAllPage(Integer page, Integer size) {
        Page<ApsMaterialShortageAnalysisDto> pageTemp = new Page<>();
        pageTemp.setSize(size);
        pageTemp.setCurrent(page);
        PageResultVo<ApsMaterialShortageAnalysisDto> restVo = new PageResultVo<>();

        return restVo;
    }


    @Override
    public ResultColPageVo<Object> getResultFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        return commonFiltrate(page, size, SchedulingResultType.APS_MATERIAL_SHORTAGE_ANALYSIS, queryViewParams,
                (pageTemp, queryWrapper, versionToChVersionArrayList) ->
                        materialShortageAnalysisMapper.queryPageList(pageTemp, queryWrapper, versionToChVersionArrayList));
    }

    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper, List<VersionToChVersion> versionToChVersionArrayList) {
        return materialShortageAnalysisMapper.searchLike(queryWrapper,versionToChVersionArrayList);
    }
}




