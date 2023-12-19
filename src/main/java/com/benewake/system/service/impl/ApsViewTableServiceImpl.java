package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsConditionTable;
import com.benewake.system.entity.ApsViewColTable;
import com.benewake.system.entity.ApsViewTable;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.entity.vo.ViewParam;
import com.benewake.system.entity.vo.ViewTableListVo;
import com.benewake.system.entity.vo.ViewTableVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsConditionTableService;
import com.benewake.system.service.ApsViewColTableService;
import com.benewake.system.service.ApsViewTableService;
import com.benewake.system.mapper.ApsViewTableMapper;
import com.benewake.system.utils.HostHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_view_table】的数据库操作Service实现
 * @createDate 2023-12-06 15:29:02
 */
@Service
public class ApsViewTableServiceImpl extends ServiceImpl<ApsViewTableMapper, ApsViewTable>
        implements ApsViewTableService {
    @Autowired
    private ApsViewTableMapper viewTableMapper;

    @Autowired
    private ApsViewColTableService viewColTableService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private ApsConditionTableService conditionTableService;

    @Override
    public ViewTableListVo getViews(Integer tableId) {
        LambdaQueryWrapper<ApsViewTable> viewTableQueryWrapper = new LambdaQueryWrapper<>();
        viewTableQueryWrapper.eq(ApsViewTable::getTableId, tableId)
                .eq(ApsViewTable::getUserName, hostHolder.getUser().getUsername());
        List<ApsViewTable> viewTables = viewTableMapper.selectList(viewTableQueryWrapper);
        return buildViewTableListVo(viewTables);
    }

    private ViewTableListVo buildViewTableListVo(List<ApsViewTable> viewTables) {
        ViewTableListVo viewTableListVo = new ViewTableListVo();
        viewTableListVo.setDefaultViewId(-1);
        List<ViewTableVo> viewTableListVos = new ArrayList<>();
        viewTables.forEach(x -> {
            if (x.getIsDefault()) {
                viewTableListVo.setDefaultViewId(x.getViewId());
                viewTableListVo.setDefaultViewName(x.getViewName());
            }
            ViewTableVo viewTableVo = new ViewTableVo();
            viewTableVo.setViewId(x.getViewId());
            viewTableVo.setUserName(x.getUserName());
            viewTableVo.setViewName(x.getViewName());
//            viewTableVo.setTableId(x.getTableId());
            viewTableListVos.add(viewTableVo);
        });
        viewTableListVo.setViewTableVos(viewTableListVos);
        return viewTableListVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveView(ViewParam viewParam) {
        String userName = hostHolder.getUser().getUsername();

        ApsViewTable viewTable = buildView(viewParam);

        if (Boolean.TRUE.equals(viewTable.getIsDefault())) {
            viewTableMapper.update(null, new LambdaUpdateWrapper<ApsViewTable>()
                    .eq(ApsViewTable::getTableId, viewParam.getTableId())
                    .eq(ApsViewTable::getUserName, userName)
                    .eq(ApsViewTable::getIsDefault, true)
                    .set(ApsViewTable::getIsDefault, false));
        }

        if (viewParam.getViewId() == null) {
            //首次保存
            verifyParamAndSave(viewParam, userName, viewTable);
            Integer viewId = viewTable.getViewId();
            return viewColTableService.saveBatch(buildViewCols(viewParam, viewId));
        }

        if (StringUtils.isEmpty(viewParam.getViewName())) {
            List<ViewColParam> cols = viewParam.getCols();
            List<Integer> viewColIds = viewColTableService.list(new LambdaQueryWrapper<ApsViewColTable>()
                            .eq(ApsViewColTable::getViewId, viewParam.getViewId())
                            .select(ApsViewColTable::getId)).stream()
                    .map(ApsViewColTable::getId)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(viewColIds)) {
                conditionTableService.remove(new LambdaQueryWrapper<ApsConditionTable>()
                        .in(ApsConditionTable::getViewColId, viewColIds));
            }
            if (CollectionUtils.isEmpty(cols)) {
                return true;
            }
            //筛选页面
            ArrayList<ApsConditionTable> conditionTables = new ArrayList<>();
            for (ViewColParam col : cols) {
                ApsConditionTable conditionTable = new ApsConditionTable();
                conditionTable.setViewColId(col.getId());
                conditionTable.setValueOperator(col.getValueOperator());
                conditionTable.setColValue(col.getColValue());
                conditionTable.setColSeq(col.getColSeq());
                conditionTables.add(conditionTable);
            }
            conditionTableService.saveBatch(conditionTables);
            return true;
        }
        Integer viewId = viewTable.getViewId();
        return handleExistingView(viewParam, userName, viewId);
    }

    @Override
    public List<String> selectColNameByViewId(Long viewId) {
        return viewTableMapper.selectColNameByViewId(viewId);
    }


    private void verifyParamAndSave(ViewParam viewParam, String userName, ApsViewTable viewTable) {
        verifyParam(viewParam, userName);
        save(viewTable);
    }

    private Boolean handleExistingView(ViewParam viewParam, String userName, Integer viewId) {
        //修改视图
        ApsViewTable apsViewTable = viewTableMapper.selectById(viewId);
        LambdaUpdateWrapper<ApsViewTable> viewTableLambdaUpdateWrapper = new LambdaUpdateWrapper<ApsViewTable>()
                .eq(ApsViewTable::getViewId, viewId)
                .eq(ApsViewTable::getUserName, userName);
        if (!apsViewTable.getViewName().equals(viewParam.getViewName())) {
            verifyParam(viewParam, userName);
            viewTableLambdaUpdateWrapper
                    .set(ApsViewTable::getViewName, viewParam.getViewName());
        }
        viewTableLambdaUpdateWrapper
                .set(ApsViewTable::getIsDefault, viewParam.getIsDefault());
        update(viewTableLambdaUpdateWrapper);
        //全量更新
        List<ApsViewColTable> viewColTableList = viewColTableService.list(new LambdaQueryWrapper<ApsViewColTable>()
                .eq(ApsViewColTable::getViewId, viewId));
        List<ApsViewColTable> viewColTables = buildViewCols(viewParam, viewId);
        // 找出要添加的元素
        List<ApsViewColTable> toAdd = viewColTables.stream()
                .filter(colTable -> viewColTableList.stream().noneMatch(table -> table.getColId().equals(colTable.getColId())))
                .map(x -> {
                    ApsViewColTable apsViewColTable = new ApsViewColTable();
                    apsViewColTable.setViewId(viewId);
                    apsViewColTable.setColId(x.getColId());
                    return apsViewColTable;
                }).collect(Collectors.toList());
        // 找出要删除的元素
        List<Integer> deleteId = viewColTableList.stream()
                .filter(table -> viewColTables.stream().noneMatch(colTable -> colTable.getColId().equals(table.getColId())))
                .map(ApsViewColTable::getId)
                .collect(Collectors.toList());

        viewColTableService.removeBatchByIds(deleteId);
        viewColTableService.saveBatch(toAdd);
        return true;
    }


    private void verifyParam(ViewParam viewParam, String userName) {
        String viewName = viewParam.getViewName();
        if (StringUtils.isEmpty(viewName)) {
            throw new BeneWakeException("视图名称不能为空");
        }
        List<ApsViewTable> viewTables = viewTableMapper
                .selectList(new LambdaQueryWrapper<ApsViewTable>()
                        .eq(ApsViewTable::getViewName, viewName)
                        .eq(ApsViewTable::getUserName, userName)
                        .eq(ApsViewTable::getTableId, viewParam.getTableId()));
        if (CollectionUtils.isNotEmpty(viewTables)) {
            throw new BeneWakeException("视图名称冲突");
        }
    }

    private List<ApsViewColTable> buildViewCols(ViewParam viewParam, Integer viewId) {
        List<ViewColParam> cols = viewParam.getCols();
        return cols.stream().map(x -> {
            ApsViewColTable viewColTable = new ApsViewColTable();
            viewColTable.setId(x.getId());
            viewColTable.setViewId(viewId);
            viewColTable.setColId(x.getColId());
//            viewColTable.setValueOperator(x.getValueOperator());
//            viewColTable.setColValue(x.getColValue());
//            viewColTable.setColSeq(x.getColSeq());
            return viewColTable;
        }).collect(Collectors.toList());
    }


    private ApsViewTable buildView(ViewParam viewParam) {
        String userName = hostHolder.getUser().getUsername();
        ApsViewTable viewTable = new ApsViewTable();
        viewTable.setUserName(userName);
        viewTable.setViewName(viewParam.getViewName());
        if (viewParam.getTableId() != null) {
            viewTable.setTableId(Math.toIntExact(viewParam.getTableId()));
        }
        if (viewParam.getViewId() != null) {
            viewTable.setViewId(Math.toIntExact(viewParam.getViewId()));
        }
        viewTable.setIsDefault(viewParam.getIsDefault());
        return viewTable;
    }
}




