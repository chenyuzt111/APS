package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.*;
import com.benewake.system.entity.dto.ApsProcessCapacityDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.entity.vo.DownloadParam;
import com.benewake.system.excel.entity.ExcelProcessCapacity;
import com.benewake.system.excel.entity.ExcelProcessNamePool;
import com.benewake.system.excel.listener.ProcessCapacityListener;
import com.benewake.system.excel.listener.ProcessPoolListener;
import com.benewake.system.excel.transfer.ProcessCapacityDtoToExcelList;
import com.benewake.system.excel.transfer.ProcessCapacityVoToExcelList;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.service.ApsProductFamilyProcessSchemeManagementService;
import com.benewake.system.transfer.ApsProcessCapacityEntityToVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_process_capacity(工序与产能表)】的数据库操作Service实现
 * @createDate 2023-10-20 15:03:52
 */
@Service
public class ApsProcessCapacityServiceImpl extends ServiceImpl<ApsProcessCapacityMapper, ApsProcessCapacity>
        implements ApsProcessCapacityService {


    @Autowired
    private ApsProcessCapacityEntityToVo apsProcessCapacityEntityToVo;

    @Autowired
    private ApsProcessCapacityMapper apsProcessCapacityMapper;

    @Autowired
    private ApsProcessNamePoolService apsProcessNamePoolService;

    @Autowired
    private ApsProcessSchemeMapper apsProcessSchemeMapper;

    @Autowired
    private ApsProductFamilyProcessSchemeManagementService managementService;

    @Autowired
    private ProcessCapacityDtoToExcelList processCapacityDtoToExcelList;

    @Autowired
    private ProcessCapacityVoToExcelList processCapacityVoToExcelList;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveOrUpdateProcessCapacityService(ApsProcessCapacityParam apsProcessCapacityParam) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        BeanUtils.copyProperties(apsProcessCapacityParam, apsProcessCapacity);
        apsProcessCapacity.setStandardTime(new BigDecimal(apsProcessCapacityParam.getStandardTime()));
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessNamePoolLambdaQueryWrapper.eq(ApsProcessNamePool::getProcessName, apsProcessCapacityParam.getProcessName());
        ApsProcessNamePool apsProcessNamePoolServiceOne = apsProcessNamePoolService.getOne(apsProcessNamePoolLambdaQueryWrapper);
        if (apsProcessNamePoolServiceOne == null) {
            throw new BeneWakeException("当前工序名称不存在");
        }
        apsProcessCapacity.setProcessId(apsProcessNamePoolServiceOne.getId());

        if (apsProcessCapacity.getId() == null) {
            //如果当前产品族有基础工艺方案并且他们的有效性为true 那么现在应该将他们的有效性全部设为false
            setSchemeStateFalse(Collections.singletonList(apsProcessCapacity.getProductFamily()));
            //删除当前所有的最优方案表
            deleteOptimal(Collections.singletonList(apsProcessCapacity.getProductFamily()));
            //保存一个新的工序与产能
            return saveProcessCapacity(apsProcessCapacity);
        }

        ApsProcessCapacity processCapacity = getById(apsProcessCapacityParam.getId());
        apsProcessCapacity.setProcessNumber(processCapacity.getProcessNumber());
        return this.updateById(apsProcessCapacity);
    }

    private void deleteOptimal(List<String> productFamily) {
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApsProductFamilyProcessSchemeManagement::getProductFamily,
                productFamily);
        managementService.remove(queryWrapper);
    }

    private void setSchemeStateFalse(List<String> productFamily) {
        LambdaUpdateWrapper<ApsProcessScheme> schemeUpdateWrapper = new LambdaUpdateWrapper<>();
        for (String family : productFamily) {
            schemeUpdateWrapper.or(i -> i.likeRight(ApsProcessScheme::getCurrentProcessScheme, family));
        }
        schemeUpdateWrapper.eq(ApsProcessScheme::getState, true)
                .set(ApsProcessScheme::getState, false);
        apsProcessSchemeMapper.update(null, schemeUpdateWrapper);
    }

    private boolean saveProcessCapacity(ApsProcessCapacity apsProcessCapacity) {
        LambdaQueryWrapper<ApsProcessCapacity> apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessCapacityLambdaQueryWrapper
                .eq(ApsProcessCapacity::getProductFamily, apsProcessCapacity.getProductFamily())
                .orderByDesc(ApsProcessCapacity::getProcessNumber)
                .last("limit 1")
                .select(ApsProcessCapacity::getProcessNumber);
        ApsProcessCapacity processCapacity = this.getOne(apsProcessCapacityLambdaQueryWrapper);
        if (processCapacity != null) {
            apsProcessCapacity.setProcessNumber(processCapacity.getProcessNumber() + 1);
        } else {
            apsProcessCapacity.setProcessNumber(1);
        }
        return this.save(apsProcessCapacity);
    }

    @Override
    public ApsProcessCapacityListVo getAllProcessCapacity(Integer page, Integer size) {
        Page<ApsProcessCapacityDto> capacityDtoPage = new Page<>();
        capacityDtoPage.setCurrent(page);
        capacityDtoPage.setSize(size);
        Page<ApsProcessCapacityDto> dtoPage = apsProcessCapacityMapper.selectPages(capacityDtoPage);
        List<ApsProcessCapacityVo> apsProcessCapacityVos = getApsProcessCapacityVos(dtoPage);

        ApsProcessCapacityListVo apsProcessCapacityListVo = new ApsProcessCapacityListVo();
        apsProcessCapacityListVo.setApsProcessCapacityVo(apsProcessCapacityVos);
        apsProcessCapacityListVo.setPage(page);
        apsProcessCapacityListVo.setSize(size);
        apsProcessCapacityListVo.setTotal(dtoPage.getTotal());
        apsProcessCapacityListVo.setPages(dtoPage.getPages());
        return apsProcessCapacityListVo;
    }

    private List<ApsProcessCapacityVo> getApsProcessCapacityVos(Page<ApsProcessCapacityDto> dtoPage) {
        List<ApsProcessCapacityDto> records = dtoPage.getRecords();
        Map<String, Integer> productFamilyToNumberMap = new HashMap<>();
        //处理序号
        List<ApsProcessCapacityVo> apsProcessCapacityVos = records.stream()
                .map(x -> apsProcessCapacityEntityToVo.convert(x))
                .peek(x -> {
                    String productFamily = x.getProductFamily();
                    Integer number = productFamilyToNumberMap.getOrDefault(productFamily, 0);
                    productFamilyToNumberMap.put(productFamily, number + 1);
                    x.setProcessNumber(number + 1);
                })
                .collect(Collectors.toList());
        String productFamily = null;
        Integer oneCount = null;
        if (CollectionUtils.isNotEmpty(apsProcessCapacityVos)) {
            //获取第一个产品族 因为第一个产品族可能不是从第一个开始取的
            ApsProcessCapacityVo apsProcessCapacityVo = apsProcessCapacityVos.get(0);
            productFamily = apsProcessCapacityVo.getProductFamily();
            if (StringUtils.isNotEmpty(productFamily)) {
                LambdaQueryWrapper<ApsProcessCapacity> apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
                apsProcessCapacityLambdaQueryWrapper.eq(ApsProcessCapacity::getProductFamily, productFamily);
                oneCount = Math.toIntExact(baseMapper.selectCount(apsProcessCapacityLambdaQueryWrapper));
            }
        }
        String finalProductFamily = productFamily;
        final long[] count = {
                apsProcessCapacityVos.stream()
                        .filter(x -> x.getProductFamily().equals(finalProductFamily))
                        .count()};
        Integer finalOneCount = oneCount;
        //转换第一行序号
        apsProcessCapacityVos = apsProcessCapacityVos.stream().peek(x -> {
            if (x.getProductFamily().equals(finalProductFamily)) {
                x.setProcessNumber((int) (finalOneCount - count[0] + 1));
                count[0]--;
            }
        }).collect(Collectors.toList());
        return apsProcessCapacityVos;
    }


    @Override
    public List<ApsProcessCapacityVo> getProcessCapacitysByproductFamily(String productFamily) {
        List<ApsProcessCapacityVo> apsProcessCapacities = apsProcessCapacityMapper
                .selectProcessCapacitysByproductFamily(productFamily);
        final int[] counter = {0};
        List<ApsProcessCapacityVo> processCapacityList = apsProcessCapacities.stream().peek(x -> {
            x.setProcessNumber(++counter[0]);
        }).collect(Collectors.toList());
        return processCapacityList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchAndUpdateByIds(List<Integer> ids) {
        LambdaQueryWrapper<ApsProcessCapacity> capacityQueryWrapper = new LambdaQueryWrapper<>();
        capacityQueryWrapper.in(ApsProcessCapacity::getId, ids);
        List<ApsProcessCapacity> apsProcessCapacities = getBaseMapper().selectList(capacityQueryWrapper);
        List<String> productFamilyList = apsProcessCapacities.stream()
                .map(ApsProcessCapacity::getProductFamily)
                .distinct()
                .collect(Collectors.toList());

        LambdaQueryWrapper<ApsProcessScheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        schemeLambdaQueryWrapper.in(ApsProcessScheme::getProcessCapacityId, ids);
        apsProcessSchemeMapper.delete(schemeLambdaQueryWrapper);
        //将所有的状态值设为false
        setSchemeStateFalse(productFamilyList);
        //删除当前的所有最终方案
        deleteOptimal(productFamilyList);
        return removeBatchByIds(ids);
    }

    @Override
    public Boolean updateProcessNumber(List<ApsProcessCapacityParam> apsProcessCapacityVo) {
        AtomicReference<Integer> number = new AtomicReference<>(1);
        List<ApsProcessCapacity> apsProcessCapacities = apsProcessCapacityVo.stream().map(x -> {
            ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
            apsProcessCapacity.setId(x.getId());
            apsProcessCapacity.setProcessNumber(number.getAndSet(number.get() + 1));
            return apsProcessCapacity;
        }).collect(Collectors.toList());
        return updateBatchById(apsProcessCapacities);
    }

    @Override
    public void downloadProcessCapacity(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("我是文件名", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<ExcelProcessCapacity> excelProcessCapacities = null;
            if (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()) {
                Page<ApsProcessCapacityDto> apsProcessCapacityDtoPage = new Page<ApsProcessCapacityDto>()
                        .setCurrent(downloadParam.getPage())
                        .setSize(downloadParam.getSize());
                Page<ApsProcessCapacityDto> dtoPage = apsProcessCapacityMapper.selectPages(apsProcessCapacityDtoPage);
                List<ApsProcessCapacityVo> apsProcessCapacityVos = getApsProcessCapacityVos(dtoPage);
                excelProcessCapacities = processCapacityVoToExcelList.convert(apsProcessCapacityVos);
            } else {
                List<ApsProcessCapacityDto> apsProcessCapacityDtos = apsProcessCapacityMapper.selectAllDtos();
                excelProcessCapacities = processCapacityDtoToExcelList.convert(apsProcessCapacityDtos);
            }
            EasyExcel.write(response.getOutputStream(), ExcelProcessCapacity.class).sheet("sheet1").doWrite(excelProcessCapacities);
        } catch (Exception e) {
            log.error("工序与产能导出失败了" + e);
            throw new BeneWakeException("工序与产能导出失败了");
        }
    }

    @Override
    public Boolean saveDataByExcel(Integer type, MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), ExcelProcessCapacity.class,
                            new ProcessCapacityListener(this, apsProcessNamePoolService, type))
                    .sheet().headRowNumber(1).doRead();
        } catch (Exception e) {
            log.error("工序与产能导入失败" + e);
            throw new BeneWakeException(e.getMessage());
        }
        return true;
    }

}




