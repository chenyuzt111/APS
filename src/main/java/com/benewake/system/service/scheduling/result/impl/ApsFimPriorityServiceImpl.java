package com.benewake.system.service.scheduling.result.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsFimPriority;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsFimPriorityDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.entity.vo.QueryViewParams;
import com.benewake.system.entity.vo.ResultColPageVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsColumnTableMapper;
import com.benewake.system.mapper.ApsFimPriorityMapper;
import com.benewake.system.mapper.ApsViewColTableMapper;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.service.scheduling.result.ApsFimPriorityService;
import com.benewake.system.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_fim_priority】的数据库操作Service实现
 * @createDate 2023-11-06 09:59:19
 */
@Service
public class ApsFimPriorityServiceImpl extends ServiceImpl<ApsFimPriorityMapper, ApsFimPriority>
        implements ApsFimPriorityService {

    @Autowired
    private ApsTableVersionService apsTableVersionService;

    @Autowired
    private ApsFimPriorityMapper apsFimPriorityMapper;

    @Autowired
    private ApsViewColTableMapper apsViewColTableMapper;

    @Autowired
    private ApsColumnTableMapper apsColumnTableMapper;

    @Override
    public ApsTableVersionService getTableVersionService() {
        return apsTableVersionService;
    }

    @Override
    public ApsViewColTableMapper getViewColTableMapper() {
        return apsViewColTableMapper;
    }

    @Override
    public ApsColumnTableMapper getColumnTableMapper() {
        return apsColumnTableMapper;
    }

    @Override
    public PageResultVo<ApsFimPriorityDto> getAllPage(Integer page, Integer size) {

        Page<ApsFimPriorityDto> apsFimPriorityPage = new Page<>();
        apsFimPriorityPage.setCurrent(page);
        apsFimPriorityPage.setSize(size);
        PageResultVo<ApsFimPriorityDto> apsFimPriorityPageResultVo = new PageResultVo<>();
        return apsFimPriorityPageResultVo;
    }



    @Override
    public void downloadFimRequest(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "fim需求优先级");
            PageResultVo<ApsFimPriorityDto> pageResultVo;
            if (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()) {
                pageResultVo = getAllPage(downloadParam.getPage(), downloadParam.getSize());
            } else {
                long size = count(null);
                pageResultVo = getAllPage(1, Math.toIntExact(size));
            }
            List<ApsFimPriorityDto> list = pageResultVo.getList();
            EasyExcel.write(response.getOutputStream(), ApsFimPriorityDto.class)
                    .sheet("sheet1").registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).doWrite(list);
        } catch (Exception e) {
            log.error("fim需求优先级导出失败" + e.getMessage());
            throw new BeneWakeException("fim需求优先级导出失败");
        }
    }

    public ResultColPageVo<Object> getResultFiltrate(Integer page, Integer size, QueryViewParams queryViewParams) {
        return commonFiltrate(page, size, queryViewParams,
                (page1, queryWrapper, versionToChVersionArrayList) ->
                        apsFimPriorityMapper.getFimPriorityFiltrate(page1, queryWrapper, versionToChVersionArrayList));
    }

    @Override
    public void defaultSort(QueryWrapper queryWrapper) {
        String customSqlSegment = queryWrapper.getCustomSqlSegment();
        if (!customSqlSegment.contains("ORDER")) {
            queryWrapper.orderByDesc("ch_version_name");
            queryWrapper.orderByAsc("f_production_id");
        }
    }

    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper, List<VersionToChVersion> versionToChVersionArrayList) {
        return apsFimPriorityMapper.searchLike(queryWrapper ,versionToChVersionArrayList);
    }
}