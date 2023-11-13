package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessScheme;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.entity.vo.ProcessSchemeEntity;
import com.benewake.system.entity.vo.ProcessSchemeManagementParam;
import com.benewake.system.entity.vo.ProcessSchemeManagementVo;
import com.benewake.system.entity.vo.ProcessSchemeManagementVoPage;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsOptimalStrategyMapper;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.service.ApsProductFamilyProcessSchemeManagementService;
import com.benewake.system.mapper.ApsProductFamilyProcessSchemeManagementMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_product_family_process_scheme_management】的数据库操作Service实现
 * @createDate 2023-10-24 11:38:49
 */
@Service
public class ApsProductFamilyProcessSchemeManagementServiceImpl extends ServiceImpl<ApsProductFamilyProcessSchemeManagementMapper, ApsProductFamilyProcessSchemeManagement>
        implements ApsProductFamilyProcessSchemeManagementService {

    @Autowired
    private ApsProductFamilyProcessSchemeManagementMapper apsProductFamilyProcessSchemeManagementMapper;

    @Autowired
    private ApsProcessCapacityMapper apsProcessCapacityMapper;

    @Autowired
    private ApsProcessSchemeMapper apsProcessSchemeMapper;

    @Autowired
    private ApsOptimalStrategyMapper apsOptimalStrategyMapper;

    @Override
    public ProcessSchemeManagementVoPage getProcessSchemeManagement(Integer pageNum, Integer size) {
        //取出当前的
        Page<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagementPage = new Page<>();
        apsProductFamilyProcessSchemeManagementPage.setSize(size);
        apsProductFamilyProcessSchemeManagementPage.setCurrent(pageNum);
        Page<ApsProductFamilyProcessSchemeManagement> page = apsProductFamilyProcessSchemeManagementMapper.selectPage(apsProductFamilyProcessSchemeManagementPage, null);
        List<ApsProductFamilyProcessSchemeManagement> records = page.getRecords();
        List<String> curList = records.stream().map(ApsProductFamilyProcessSchemeManagement::getCurProcessSchemeName).distinct().collect(Collectors.toList());
        List<String> opList = records.stream().map(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName).collect(Collectors.toList());
        curList.addAll(opList);
        ProcessSchemeManagementVoPage processSchemeManagementVoPage = new ProcessSchemeManagementVoPage();
        if (CollectionUtils.isEmpty(opList)) {
            processSchemeManagementVoPage.setProcessSchemeManagementVos(null);
            processSchemeManagementVoPage.setPage(pageNum);
            processSchemeManagementVoPage.setSize(size);
            return processSchemeManagementVoPage;
        }
        LambdaQueryWrapper<ApsProcessScheme> apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeLambdaQueryWrapper.in(ApsProcessScheme::getCurrentProcessScheme, curList);
        List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeMapper
                .selectList(apsProcessSchemeLambdaQueryWrapper);

        Map<String, Integer> collect = apsProcessSchemes.stream()
                .collect(Collectors.toMap(
                        ApsProcessScheme::getCurrentProcessScheme,
                        ApsProcessScheme::getId,
                        (existing, replacement) -> existing
                ));
        ArrayList<ProcessSchemeManagementVo> processSchemeManagementVos = new ArrayList<>();
        for (ApsProductFamilyProcessSchemeManagement record : records) {
            String curProcessSchemeName = record.getCurProcessSchemeName();
            Integer curId = collect.get(curProcessSchemeName);
            String optimalProcessSchemeName = record.getOptimalProcessSchemeName();
            Integer opId = collect.get(optimalProcessSchemeName);
            ProcessSchemeManagementVo processSchemeManagementVo = new ProcessSchemeManagementVo();

            processSchemeManagementVo.setProductionLineBalanceRate(record.getProductionLineBalanceRate().multiply(new BigDecimal(100)).toString() + "%");
            processSchemeManagementVo.setManId(record.getId());
            processSchemeManagementVo.setId(curId);
            processSchemeManagementVo.setCurrentProcessScheme(record.getCurProcessSchemeName());
            processSchemeManagementVo.setOptimalId(opId);
            processSchemeManagementVo.setOptimalProcessPlan(record.getOptimalProcessSchemeName());
            processSchemeManagementVo.setProductFamily(record.getProductFamily());
            processSchemeManagementVo.setNumber(record.getNumber());
            processSchemeManagementVo.setOrderNumber(record.getOrderNumber());
            processSchemeManagementVo.setCompletionTime(record.getCompletionTime());
            processSchemeManagementVo.setTotalReleaseTime(record.getTotalReleaseTime());
            processSchemeManagementVo.setReleasableStaffCount(record.getReleasableStaffCount());
            processSchemeManagementVos.add(processSchemeManagementVo);
        }

        processSchemeManagementVoPage.setProcessSchemeManagementVos(processSchemeManagementVos);
        processSchemeManagementVoPage.setPage(pageNum);
        processSchemeManagementVoPage.setSize(size);
//        Long aLong = apsProductFamilyProcessSchemeManagementMapper.selectCount(null);
        processSchemeManagementVoPage.setTotal(page.getTotal());
        processSchemeManagementVoPage.setPages(page.getPages());
        return processSchemeManagementVoPage;
    }

    @Override
    public Boolean setOrderNumber(ProcessSchemeManagementParam processSchemeManagementParam) {
        //经济批量
        Integer orderNumber = processSchemeManagementParam.getOrderNumber();
        ApsProductFamilyProcessSchemeManagement apsProductFamilyProcessSchemeManagement = this.baseMapper.selectById(processSchemeManagementParam.getManId());
        if (apsProductFamilyProcessSchemeManagement == null) {
            throw new BeneWakeException("该行数据不存在");
        }
        String productFamily = apsProductFamilyProcessSchemeManagement.getProductFamily();
        Integer number = apsProductFamilyProcessSchemeManagement.getNumber();
        //查出同样的版本
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProductFamilyProcessSchemeManagementLambdaQueryWrapper.eq(ApsProductFamilyProcessSchemeManagement::getProductFamily, productFamily)
                .eq(ApsProductFamilyProcessSchemeManagement::getNumber, number);
        List<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagements =
                apsProductFamilyProcessSchemeManagementMapper.selectList(apsProductFamilyProcessSchemeManagementLambdaQueryWrapper);
        List<String> curProcessSchemeNameList = apsProductFamilyProcessSchemeManagements.stream().map(ApsProductFamilyProcessSchemeManagement::getCurProcessSchemeName).collect(Collectors.toList());
        List<ProcessSchemeEntity> processSchemeEntityList = apsProcessSchemeMapper.selectEmployeeTime(curProcessSchemeNameList);
        Map<String, List<ProcessSchemeEntity>> processSchemeMap = processSchemeEntityList.stream()
                .collect(Collectors.groupingBy(ProcessSchemeEntity::getCurrentProcessScheme));
        for (ApsProductFamilyProcessSchemeManagement productFamilyProcessSchemeManagement : apsProductFamilyProcessSchemeManagements) {
            getRes(orderNumber, number, processSchemeMap, productFamilyProcessSchemeManagement);
        }
        return updateBatchById(apsProductFamilyProcessSchemeManagements);
    }

    private void getRes(Integer orderNumber, Integer number, Map<String, List<ProcessSchemeEntity>> processSchemeMap, ApsProductFamilyProcessSchemeManagement productFamilyProcessSchemeManagement) {
        String curProcessSchemeName = productFamilyProcessSchemeManagement.getCurProcessSchemeName();
        List<ProcessSchemeEntity> processSchemeEntities = processSchemeMap.get(curProcessSchemeName);
        BigDecimal sumOfStandardTime = processSchemeEntities.stream()
                .map(ProcessSchemeEntity::getStandardTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //每个员工的map和工时和
        Map<String, BigDecimal> employeeStandardTimeSumMap = processSchemeEntities.stream()
                .collect(Collectors.groupingBy(
                        ProcessSchemeEntity::getEmployeeName,
                        Collectors.reducing(BigDecimal.ZERO, ProcessSchemeEntity::getStandardTime, BigDecimal::add)
                ));
        //员工最大工时
        BigDecimal maxStandardTime = employeeStandardTimeSumMap.values().stream()
                .max(BigDecimal::compareTo).get();
        //产线平衡率
        BigDecimal result = sumOfStandardTime.divide(maxStandardTime.multiply(new BigDecimal(number)), 8, RoundingMode.HALF_UP);
        //订单完成时间
        BigDecimal multiply = maxStandardTime.multiply(new BigDecimal(orderNumber));
        //可以释放人数
        long countEqualMaxStandardTime = employeeStandardTimeSumMap.values().stream()
                .filter(value -> value.compareTo(maxStandardTime) == 0)
                .count();
        long releasableStaffCount = number - countEqualMaxStandardTime;
        //释放总时间
        BigDecimal differenceSum = employeeStandardTimeSumMap.values().stream()
                .map(maxStandardTime::subtract)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        differenceSum = differenceSum.multiply(new BigDecimal(orderNumber));
        productFamilyProcessSchemeManagement.setOrderNumber(orderNumber);
        productFamilyProcessSchemeManagement.setProductionLineBalanceRate(result);
        productFamilyProcessSchemeManagement.setCompletionTime(multiply);
        productFamilyProcessSchemeManagement.setReleasableStaffCount((int) releasableStaffCount);
        productFamilyProcessSchemeManagement.setTotalReleaseTime(differenceSum.doubleValue());
    }
}



