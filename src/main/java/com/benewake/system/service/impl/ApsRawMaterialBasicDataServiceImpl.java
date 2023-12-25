package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsRawMaterialBasicData;
import com.benewake.system.entity.dto.ApsRawMaterialBasicDataDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsRawMaterialBasicDataParam;
import com.benewake.system.entity.vo.ApsRawMaterialBasicDataVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.excel.entity.ExcelRawMaterialBasicDataTemplate;
import com.benewake.system.excel.listener.RawMaterialListener;
import com.benewake.system.excel.transfer.ExcelRawMaterialToPo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsRawMaterialBasicDataMapper;
import com.benewake.system.service.ApsRawMaterialBasicDataService;
import com.benewake.system.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_raw_material_basic_data】的数据库操作Service实现
 * @createDate 2023-12-04 09:27:44
 */
@Slf4j
@Service
public class ApsRawMaterialBasicDataServiceImpl extends ServiceImpl<ApsRawMaterialBasicDataMapper, ApsRawMaterialBasicData>
        implements ApsRawMaterialBasicDataService {

    @Autowired
    private ApsRawMaterialBasicDataMapper rawMaterialBasicDataMapper;

    @Autowired
    private ExcelRawMaterialToPo excelRawMaterialToPo;

    @Override
    public PageResultVo<ApsRawMaterialBasicDataVo> getRawMaterial(String name, Integer page, Integer size) {
        Page<ApsRawMaterialBasicDataDto> materialBasicDataVoPage = new Page<>(page, size);
        Page<ApsRawMaterialBasicDataDto> voPage = rawMaterialBasicDataMapper.selectListPage(materialBasicDataVoPage);
        PageResultVo<ApsRawMaterialBasicDataVo> voPageResultVo = buildPageListRestVo(voPage);
        return voPageResultVo;
    }

    @Override
    public boolean addOrUpdateRawMaterial(ApsRawMaterialBasicDataParam param) {
        ApsRawMaterialBasicData rawMaterialBasicData = buildRawMaterialBasicPo(param);
        if (rawMaterialBasicData.getId() == null) {
            return save(rawMaterialBasicData);
        } else {
            return updateById(rawMaterialBasicData);
        }
    }

    @Override
    public void downloadRawMaterial(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "原材料数据导出");
            PageResultVo<ApsRawMaterialBasicDataVo> rawMaterial = null;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                long size = this.count(null);
                rawMaterial = getRawMaterial(null, 1, Math.toIntExact(size));
            } else {
                rawMaterial = getRawMaterial(null, downloadParam.getPage(), downloadParam.getSize());
            }
            List<ApsRawMaterialBasicDataVo> list = rawMaterial.getList();
            EasyExcel.write(response.getOutputStream(), ApsRawMaterialBasicDataVo.class)
                    .sheet("sheet1").registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(list);
        } catch (Exception e) {
            log.error("原材料数据导出失败" + e.getMessage());
            throw new BeneWakeException("原材料数据导出失败");
        }
    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelRawMaterialBasicDataTemplate.class
                            , new RawMaterialListener(excelRawMaterialToPo, type, this))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            if (!(e instanceof BeneWakeException)) {
                log.error("原材料数据保存失败" + LocalDateTime.now() + "---" + e.getMessage());
                throw new BeneWakeException("原材料数据保存失败");
            }
            log.info("原材料数据保存失败" + LocalDateTime.now() + "---" + e.getMessage());
            throw new BeneWakeException(e.getMessage());
        }
        return true;
    }

    private ApsRawMaterialBasicData buildRawMaterialBasicPo(ApsRawMaterialBasicDataParam param) {
        ApsRawMaterialBasicData rawMaterialBasicData = new ApsRawMaterialBasicData();
        rawMaterialBasicData.setId(param.getId());
        rawMaterialBasicData.setFMaterialCode(param.getMaterialCode());
        rawMaterialBasicData.setFMaterialProperty(param.getMaterialProperty());
        rawMaterialBasicData.setFMaterialGroup(param.getMaterialGroup());
        rawMaterialBasicData.setFProcurementLeadTime(param.getProcurementLeadTime());
        rawMaterialBasicData.setFMoq(param.getMoq());
        rawMaterialBasicData.setFMpq(param.getMpq());
        rawMaterialBasicData.setFSafetyStock(param.getSafetyStock());
        return rawMaterialBasicData;
    }

    private PageResultVo<ApsRawMaterialBasicDataVo> buildPageListRestVo(Page<ApsRawMaterialBasicDataDto> voPage) {
        PageResultVo<ApsRawMaterialBasicDataVo> voPageResultVo = new PageResultVo<>();
//        List<ApsRawMaterialBasicDataDto> records = voPage.getRecords();
//        List<ApsRawMaterialBasicDataVo> apsRawMaterialBasicDataVos = records.stream().map(x -> {
//            ApsRawMaterialBasicDataVo materialBasicDataVo = new ApsRawMaterialBasicDataVo();
//            materialBasicDataVo.setId(x.getId());
//            materialBasicDataVo.setMaterialCode(x.getFMaterialCode());
//            materialBasicDataVo.setMaterialName(x.getFMaterialName());
//            materialBasicDataVo.setMaterialProperty(x.getFMaterialProperty());
//            materialBasicDataVo.setMaterialGroup(x.getFMaterialGroup());
//            materialBasicDataVo.setProcurementLeadTime(x.getFProcurementLeadTime());
//            materialBasicDataVo.setMoq(x.getFMoq());
//            materialBasicDataVo.setMpq(x.getFMpq());
//            materialBasicDataVo.setSafetyStock(x.getFSafetyStock());
//            return materialBasicDataVo;
//        }).collect(Collectors.toList());
//        voPageResultVo.setList(apsRawMaterialBasicDataVos);
//        voPageResultVo.setPage(Math.toIntExact(voPage.getCurrent()));
//        voPageResultVo.setSize(Math.toIntExact(voPage.getSize()));
//        voPageResultVo.setPages(voPage.getPages());
//        voPageResultVo.setTotal(voPage.getTotal());
        return voPageResultVo;
    }

    @Override
    public Page selectPageLists(Page<Object> page, QueryWrapper<Object> wrapper) {
        return rawMaterialBasicDataMapper.selectPageLists(page ,wrapper);
    }

    @Override
    public List<Object> searchLike(QueryWrapper<Object> queryWrapper) {
        return rawMaterialBasicDataMapper.searchLike(queryWrapper);
    }
}




