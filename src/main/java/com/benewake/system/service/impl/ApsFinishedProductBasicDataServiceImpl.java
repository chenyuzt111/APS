package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsFinishedProductBasicData;
import com.benewake.system.entity.dto.ApsFinishedProductBasicDataDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsFinishedProductBasicDataParam;
import com.benewake.system.entity.vo.ApsFinishedProductBasicDataVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.entity.vo.PageResultVo;
import com.benewake.system.excel.entity.ExcelFinishedProductTemplate;
import com.benewake.system.excel.listener.FinishedProductListener;
import com.benewake.system.excel.transfer.ExcelFinishedProductToPo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsFinishedProductBasicDataMapper;
import com.benewake.system.service.ApsFinishedProductBasicDataService;
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
 * @description 针对表【aps_finished_product_basic_data】的数据库操作Service实现
 * @createDate 2023-10-20 11:27:46
 */
@Slf4j
@Service
public class ApsFinishedProductBasicDataServiceImpl extends ServiceImpl<ApsFinishedProductBasicDataMapper, ApsFinishedProductBasicData>
        implements ApsFinishedProductBasicDataService {

    @Autowired
    private ApsFinishedProductBasicDataMapper finishedProductBasicDataMapper;

    @Autowired
    private ExcelFinishedProductToPo excelFinishedProductToPo;

    @Override
    public PageResultVo<ApsFinishedProductBasicDataVo> getFinishedProduct(String name, Integer page, Integer size) {
        Page<ApsFinishedProductBasicDataDto> productBasicDataDtoPage = new Page<>();
        productBasicDataDtoPage.setSize(size);
        productBasicDataDtoPage.setCurrent(page);
        Page<ApsFinishedProductBasicDataDto> basicDataDtoPage = finishedProductBasicDataMapper.selectListPage(productBasicDataDtoPage);
        PageResultVo<ApsFinishedProductBasicDataVo> dataVoPageResultVo = buildPageListRestVo(basicDataDtoPage);
        return dataVoPageResultVo;
    }

    @Override
    public boolean addOrUpdateFinishedProduct(ApsFinishedProductBasicDataParam param) {
        ApsFinishedProductBasicData finishedProductBasicData = buildFinishedProduct(param);
        if (finishedProductBasicData.getId() == null) {
            return save(finishedProductBasicData);
        } else {
            return updateById(finishedProductBasicData);
        }
    }

    @Override
    public void downloadFinishedProduct(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            ResponseUtil.setFileResp(response, "成品基础数据导出");
            PageResultVo<ApsFinishedProductBasicDataVo> finishedProduct = null;
            if (downloadParam.getType() == ExcelOperationEnum.ALL_PAGES.getCode()) {
                long size = this.count(null);
                finishedProduct = getFinishedProduct(null, 1, Math.toIntExact(size));
            } else {
                finishedProduct = getFinishedProduct(null, downloadParam.getPage(), downloadParam.getSize());
            }
            EasyExcel.write(response.getOutputStream(), ApsFinishedProductBasicDataVo.class)
                    .sheet("sheet1").registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .doWrite(finishedProduct.getList());
        } catch (Exception e) {
            log.error("成品基础数据导出失败" + e.getMessage() + " ----" + LocalDateTime.now());
            throw new BeneWakeException("成品基础数据导出失败");
        }
    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelFinishedProductTemplate.class,
                    new FinishedProductListener(excelFinishedProductToPo ,type ,this))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            if (!(e instanceof BeneWakeException)) {
                log.error("成品基础数据导入失败" + e.getMessage() + "---" + LocalDateTime.now());
                throw new BeneWakeException("成品基础数据导入失败");
            }
            log.info("成品基础数据导入失败" + e.getMessage() + "---" + LocalDateTime.now());
            throw new BeneWakeException(e.getMessage());
        }
        return true;
    }

    private ApsFinishedProductBasicData buildFinishedProduct(ApsFinishedProductBasicDataParam param) {
        ApsFinishedProductBasicData finishedProductBasicData = new ApsFinishedProductBasicData();
        finishedProductBasicData.setId(param.getId());
        finishedProductBasicData.setFMaterialCode(param.getMaterialCode());
        finishedProductBasicData.setFMaterialProperty(param.getMaterialProperty());
        finishedProductBasicData.setFMaterialGroup(param.getMaterialGroup());
        finishedProductBasicData.setFProductType(param.getProductType());
        finishedProductBasicData.setFProductFamily(param.getProductFamily());
        finishedProductBasicData.setFPackagingMethod(param.getPackagingMethod());
        finishedProductBasicData.setFMaxAssemblyPersonnel(param.getMaxAssemblyPersonnel());
        finishedProductBasicData.setFMinAssemblyPersonnel(param.getMinAssemblyPersonnel());
        finishedProductBasicData.setFMaxTestingPersonnel(param.getMaxTestingPersonnel());
        finishedProductBasicData.setFMinTestingPersonnel(param.getMinTestingPersonnel());
        finishedProductBasicData.setFMaxPackagingPersonnel(param.getMaxPackagingPersonnel());
        finishedProductBasicData.setFMinPackagingPersonnel(param.getMinPackagingPersonnel());
        finishedProductBasicData.setFMoq(param.getMoq());
        finishedProductBasicData.setFMpq(param.getMpq());
        finishedProductBasicData.setFSafetyStock(param.getSafetyStock());
        return finishedProductBasicData;
    }

    private PageResultVo<ApsFinishedProductBasicDataVo> buildPageListRestVo(Page<ApsFinishedProductBasicDataDto> basicDataDtoPage) {
        List<ApsFinishedProductBasicDataDto> records = basicDataDtoPage.getRecords();
        List<ApsFinishedProductBasicDataVo> dataVos = records.stream().map(x -> {
            ApsFinishedProductBasicDataVo finishedProductBasicDataVo = new ApsFinishedProductBasicDataVo();
            finishedProductBasicDataVo.setId(x.getId());
            finishedProductBasicDataVo.setMaterialCode(x.getFMaterialCode());
            finishedProductBasicDataVo.setMaterialName(x.getFMaterialName());
            finishedProductBasicDataVo.setMaterialProperty(x.getFMaterialProperty());
            finishedProductBasicDataVo.setMaterialGroup(x.getFMaterialGroup());
            finishedProductBasicDataVo.setProductType(x.getFProductType());
            finishedProductBasicDataVo.setProductFamily(x.getFProductFamily());
            finishedProductBasicDataVo.setPackagingMethod(x.getFPackagingMethod());
            finishedProductBasicDataVo.setMaxAssemblyPersonnel(x.getFMaxAssemblyPersonnel());
            finishedProductBasicDataVo.setMinAssemblyPersonnel(x.getFMinAssemblyPersonnel());
            finishedProductBasicDataVo.setMaxTestingPersonnel(x.getFMaxTestingPersonnel());
            finishedProductBasicDataVo.setMinTestingPersonnel(x.getFMinTestingPersonnel());
            finishedProductBasicDataVo.setMaxPackagingPersonnel(x.getFMaxPackagingPersonnel());
            finishedProductBasicDataVo.setMinPackagingPersonnel(x.getFMinPackagingPersonnel());
            finishedProductBasicDataVo.setMoq(x.getFMoq());
            finishedProductBasicDataVo.setMpq(x.getFMpq());
            finishedProductBasicDataVo.setSafetyStock(x.getFSafetyStock());
            return finishedProductBasicDataVo;
        }).collect(Collectors.toList());
        PageResultVo<ApsFinishedProductBasicDataVo> resultVo = new PageResultVo<>();
        resultVo.setList(dataVos);
        resultVo.setPage(Math.toIntExact(basicDataDtoPage.getCurrent()));
        resultVo.setSize(Math.toIntExact(basicDataDtoPage.getSize()));
        resultVo.setTotal(basicDataDtoPage.getTotal());
        resultVo.setPages(basicDataDtoPage.getPages());
        return resultVo;
    }
}




