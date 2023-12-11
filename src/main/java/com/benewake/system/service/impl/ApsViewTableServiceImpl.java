package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsViewColTable;
import com.benewake.system.entity.ApsViewTable;
import com.benewake.system.entity.vo.ViewColParam;
import com.benewake.system.entity.vo.ViewParam;
import com.benewake.system.entity.vo.ViewTableListVo;
import com.benewake.system.entity.vo.ViewTableVo;
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

import java.util.ArrayList;
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
        if (viewTable.getIsDefault() != null && viewTable.getIsDefault()) {
            viewTableMapper.update(null, new LambdaUpdateWrapper<ApsViewTable>()
                    .eq(ApsViewTable::getTableId, viewParam.getTableId())
                    .eq(ApsViewTable::getUserName, userName)
                    .eq(ApsViewTable::getIsDefault, true)
                    .set(ApsViewTable::getIsDefault, false));
        }
        if (viewParam.getViewId() == null) {
            verifyParam(viewParam, userName);
            save(viewTable);
            //需要save 回填viewTable Id
            List<ApsViewColTable> viewColTables = buildViewCols(viewParam, viewTable.getViewId());
            return viewColTableService.saveBatch(viewColTables);
        } else {
            if (StringUtils.isEmpty(viewParam.getViewName())) {
                //保存筛选
                List<ApsViewColTable> viewColTables = buildViewCols(viewParam, viewTable.getViewId());
                return viewColTableService.updateBatchById(viewColTables);
            } else {
                verifyParam(viewParam, userName);
                //修改视图列
                List<ApsViewColTable> viewColTables = buildViewCols(viewParam, viewTable.getViewId());
                viewColTableService.remove(new LambdaQueryWrapper<ApsViewColTable>()
                        .eq(ApsViewColTable::getViewId, viewTable.getViewId()));
                viewColTableService.saveBatch(viewColTables);
                return updateById(viewTable);
            }
        }
    }

    private void verifyParam(ViewParam viewParam, String userName) {
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




