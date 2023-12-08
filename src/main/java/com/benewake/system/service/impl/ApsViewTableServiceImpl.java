package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsColumnTable;
import com.benewake.system.entity.ApsViewColTable;
import com.benewake.system.entity.ApsViewTable;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.entity.vo.ViewParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsViewColTableService;
import com.benewake.system.service.ApsViewTableService;
import com.benewake.system.mapper.ApsViewTableMapper;
import com.benewake.system.utils.HostHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    public List<ApsViewTable> getViews(Integer tableId) {
        LambdaQueryWrapper<ApsViewTable> viewTableQueryWrapper = new LambdaQueryWrapper<>();
        viewTableQueryWrapper.eq(ApsViewTable::getTableId, tableId)
                .eq(ApsViewTable::getUserName, hostHolder.getUser().getUsername());
        return viewTableMapper.selectList(viewTableQueryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveView(ViewParam viewParam) {
        String userName = hostHolder.getUser().getUsername();
        String viewName = viewParam.getViewName();
        if (StringUtils.isEmpty(viewName)) {
            throw new BeneWakeException("视图名称不能为空");
        }
        List<ApsViewTable> viewTables = viewTableMapper
                .selectList(new LambdaQueryWrapper<ApsViewTable>()
                        .eq(ApsViewTable::getViewName, viewName)
                        .eq(ApsViewTable::getUserName, userName));
        if (CollectionUtils.isNotEmpty(viewTables)) {
            throw new BeneWakeException("视图名称冲突");
        }
        ApsViewTable viewTable = buildView(viewParam);
        if (viewParam.getViewId() == null) {
            save(viewTable);
            //需要save 回填viewTable
            List<ApsViewColTable> viewColTables = buildViewCols(viewParam, viewTable.getViewId());
            return viewColTableService.saveBatch(viewColTables);
        } else {
            List<ApsViewColTable> viewColTables = buildViewCols(viewParam, viewTable.getViewId());
            return viewColTableService.updateBatchById(viewColTables);
        }
    }

    private List<ApsViewColTable> buildViewCols(ViewParam viewParam, Integer viewId) {
        List<ViewColParam> cols = viewParam.getCols();
        return cols.stream().map(x -> {
            ApsViewColTable viewColTable = new ApsViewColTable();
            viewColTable.setId(x.getId());
            viewColTable.setViewId(viewId);
            viewColTable.setColId(x.getColId());
            viewColTable.setValueOperator(x.getValueOperator());
            viewColTable.setColValue(x.getColValue());
            viewColTable.setColSeq(x.getColSeq());
            return viewColTable;
        }).collect(Collectors.toList());
    }

    private ApsViewTable buildView(ViewParam viewParam) {
        String userName = hostHolder.getUser().getUsername();
        ApsViewTable viewTable = new ApsViewTable();
        viewTable.setUserName(userName);
        viewTable.setViewName(viewParam.getViewName());
        viewTable.setTableId(Math.toIntExact(viewParam.getTableId()));
        return viewTable;
    }
}




