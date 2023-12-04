package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsSemiFinishedBasicData;
import com.benewake.system.entity.dto.ApsSemiFinishedBasicDataDto;
import com.benewake.system.entity.vo.ApsSemiFinishedBasicDataParam;
import com.benewake.system.entity.vo.ApsSemiFinishedBasicDataVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsSemiFinishedBasicDataMapper;
import com.benewake.system.service.ApsSemiFinishedBasicDataService;
import com.benewake.system.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_semi_finished_basic_data】的数据库操作Service实现
 * @createDate 2023-12-04 13:35:18
 */
@Service
public class ApsSemiFinishedBasicDataServiceImpl extends ServiceImpl<ApsSemiFinishedBasicDataMapper, ApsSemiFinishedBasicData>
        implements ApsSemiFinishedBasicDataService {


    @Autowired
    private ApsSemiFinishedBasicDataMapper semiFinishedBasicDataMapper;

    @Override
    public PageResultVo<ApsSemiFinishedBasicDataVo> getSemiFinished(String name, Integer page, Integer size) {
        Page<ApsSemiFinishedBasicDataDto> semiFinishedBasicDataDtoPage = new Page<>();
        Page<ApsSemiFinishedBasicDataDto> dataDtoPage = semiFinishedBasicDataDtoPage.setCurrent(page).setSize(size);
        dataDtoPage = semiFinishedBasicDataMapper.selectListPage(dataDtoPage);
        PageResultVo<ApsSemiFinishedBasicDataVo> resultVo = buildPageResultVo(dataDtoPage);
        return resultVo;
    }

    @Override
    public boolean addOrUpdateSemiFinished(ApsSemiFinishedBasicDataParam param) {
        ApsSemiFinishedBasicData semiFinishedBasicData = buildSemiFinishedPo(param);
        if (semiFinishedBasicData.getId() == null) {
            return save(semiFinishedBasicData);
        } else {
            return updateById(semiFinishedBasicData);
        }
    }

    @Override
    public void downloadSemiFinished(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response ,"半成品基础数据");
        } catch (Exception e) {
            log.error("半成品基础数据导出失败" + e.getMessage() + "---" + LocalDateTime.now());
            throw new BeneWakeException("半成品基础数据导出失败");
        }
    }


    private ApsSemiFinishedBasicData buildSemiFinishedPo(ApsSemiFinishedBasicDataParam param) {
        ApsSemiFinishedBasicData semiFinishedBasicData = new ApsSemiFinishedBasicData();
        semiFinishedBasicData.setId(param.getId());
        semiFinishedBasicData.setFMaterialCode(param.getMaterialCode());
        semiFinishedBasicData.setFMaterialProperty(param.getMaterialProperty());
        semiFinishedBasicData.setFMaterialGroup(param.getMaterialGroup());
        semiFinishedBasicData.setFProductType(param.getProductType());
        semiFinishedBasicData.setFProcurementLeadTime(param.getProcurementLeadTime());
        semiFinishedBasicData.setFMoq(param.getMoq());
        semiFinishedBasicData.setFMpq(param.getMpq());
        semiFinishedBasicData.setFSafetyStock(param.getSafetyStock());
        return semiFinishedBasicData;
    }

    private PageResultVo<ApsSemiFinishedBasicDataVo> buildPageResultVo(Page<ApsSemiFinishedBasicDataDto> dataDtoPage) {
        PageResultVo<ApsSemiFinishedBasicDataVo> resultVo = new PageResultVo<>();
        List<ApsSemiFinishedBasicDataVo> semiFinishedBasicDataVos = dataDtoPage.getRecords().stream().map(x -> {
            ApsSemiFinishedBasicDataVo apsSemiFinishedBasicDataVo = new ApsSemiFinishedBasicDataVo();
            apsSemiFinishedBasicDataVo.setId(x.getId());
            apsSemiFinishedBasicDataVo.setMaterialCode(x.getFMaterialCode());
            apsSemiFinishedBasicDataVo.setMaterialName(x.getFMaterialName());
            apsSemiFinishedBasicDataVo.setMaterialProperty(x.getFMaterialProperty());
            apsSemiFinishedBasicDataVo.setMaterialGroup(x.getFMaterialGroup());
            apsSemiFinishedBasicDataVo.setProductType(x.getFProductType());
            apsSemiFinishedBasicDataVo.setProcurementLeadTime(x.getFProcurementLeadTime());
            apsSemiFinishedBasicDataVo.setMoq(x.getFMoq());
            apsSemiFinishedBasicDataVo.setMpq(x.getFMpq());
            apsSemiFinishedBasicDataVo.setSafetyStock(x.getFSafetyStock());
            return apsSemiFinishedBasicDataVo;
        }).collect(Collectors.toList());
        resultVo.setList(semiFinishedBasicDataVos);
        resultVo.setPage(Math.toIntExact(dataDtoPage.getCurrent()));
        resultVo.setSize(Math.toIntExact(dataDtoPage.getSize()));
        resultVo.setTotal(dataDtoPage.getTotal());
        resultVo.setPages(dataDtoPage.getPages());
        return resultVo;
    }
}




