package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOptimalStrategy;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessScheme;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.entity.vo.*;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.mapper.ApsProductFamilyProcessSchemeManagementMapper;
import com.benewake.system.service.ApsOptimalStrategyService;
import com.benewake.system.service.ApsProcessSchemeService;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.service.ApsProductFamilyProcessSchemeManagementService;
import com.benewake.system.utils.StringUtils;
import io.swagger.models.auth.In;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_process_scheme】的数据库操作Service实现
 * @createDate 2023-10-21 17:38:05
 */
@Service
public class ApsProcessSchemeServiceImpl extends ServiceImpl<ApsProcessSchemeMapper, ApsProcessScheme>
        implements ApsProcessSchemeService {

    @Autowired
    private ApsProcessSchemeMapper apsProcessSchemeMapper;

    @Autowired
    private ApsProcessCapacityMapper apsProcessCapacityMapper;

    @Autowired
    private ApsProductFamilyProcessSchemeManagementMapper apsProductFamilyProcessSchemeManagementMapper;

    @Autowired
    private ApsOptimalStrategyService apsOptimalStrategyService;

    @Autowired
    private ApsProductFamilyProcessSchemeManagementService apsProductFamilyProcessSchemeManagementService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveProcessScheme(ApsProcessSchemeParams apsProcessSchemeParams) {
        List<ApsProcessSchemeParam> apsProcessSchemeParam = apsProcessSchemeParams.getApsProcessSchemeParam();
        List<String> employeeList = apsProcessSchemeParam.stream().map(ApsProcessSchemeParam::getEmployeeName).distinct().collect(Collectors.toList());
        int size = employeeList.size();
        if (size != apsProcessSchemeParams.getNumber()) {
            throw new BeneWakeException("输入人数 与分配人数 不一致~~~~~~~");
        }
        List<Integer> processCapacityIds = apsProcessSchemeParam.stream()
                .map(ApsProcessSchemeParam::getId)
                .collect(Collectors.toList());
        Integer number = apsProcessSchemeParams.getNumber();
        List<String> processSchemes = apsProcessSchemeMapper.selectProcessScheme(processCapacityIds, number);
        String currentProcessScheme;
        if (CollectionUtils.isNotEmpty(processSchemes)
                && StringUtils.isNotBlank(processSchemes.get(0))) {
            currentProcessScheme = StringUtils.incrementAndExtractLast(processSchemes);
        } else {
            ApsProcessCapacity apsProcessCapacity = apsProcessCapacityMapper.selectOne(new LambdaQueryWrapper<ApsProcessCapacity>()
                    .eq(ApsProcessCapacity::getId, apsProcessSchemeParam.get(0).getId())
                    .select(ApsProcessCapacity::getProductFamily));
            currentProcessScheme = apsProcessCapacity.getProductFamily() + "-" + number + "人工艺方案1";
        }

        String finalCurrentProcessScheme = currentProcessScheme;
        List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeParam.stream().map(x -> {
            ApsProcessScheme apsProcessScheme = new ApsProcessScheme();
            apsProcessScheme.setCurrentProcessScheme(finalCurrentProcessScheme);
            apsProcessScheme.setProcessCapacityId(x.getId());
            apsProcessScheme.setEmployeeName(x.getEmployeeName());
            apsProcessScheme.setNumber(number);
            return apsProcessScheme;
        }).collect(Collectors.toList());
        saveProducSchemeManagement(apsProcessSchemeParam, number, currentProcessScheme);
        ApsProcessCapacity apsProcessCapacity = apsProcessCapacityMapper.selectById(processCapacityIds.get(0));

        ApsOptimalStrategy apsOptimalStrategy = new ApsOptimalStrategy();
        apsOptimalStrategy.setProductFamily(apsProcessCapacity.getProductFamily());
        apsOptimalStrategy.setNumber(number);
        apsOptimalStrategy.setStrategy(1);

        apsOptimalStrategyService.ifInsert(apsOptimalStrategy);
        if (!saveBatch(apsProcessSchemes)) {
            throw new BeneWakeException("保存失败");
        }
        return currentProcessScheme;
    }

    private void saveProducSchemeManagement(List<ApsProcessSchemeParam> apsProcessSchemeParam, Integer number, String currentProcessScheme) {
        //填充最优表
        String productFamily = apsProcessSchemeParam.get(0).getProductFamily();
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProcessSchemeManagementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeManagementLambdaQueryWrapper.eq(ApsProductFamilyProcessSchemeManagement::getNumber, number)
                .eq(ApsProductFamilyProcessSchemeManagement::getProductFamily, productFamily);
        List<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagements =
                apsProductFamilyProcessSchemeManagementMapper.selectList(apsProcessSchemeManagementLambdaQueryWrapper);
        ApsProductFamilyProcessSchemeManagement apsProductFamilyProcessSchemeManagement = new ApsProductFamilyProcessSchemeManagement();
        apsProductFamilyProcessSchemeManagement.setCurProcessSchemeName(currentProcessScheme);
        apsProductFamilyProcessSchemeManagement.setNumber(number);
        apsProductFamilyProcessSchemeManagement.setProductFamily(productFamily);

        //计算不需要经济批量的
        //每个员工的map和工时和
        Map<String, BigDecimal> employeeStandardTimeSumMap = apsProcessSchemeParam.stream()
                .collect(Collectors.groupingBy(
                        ApsProcessSchemeParam::getEmployeeName,
                        Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeParam::getStandardTime, BigDecimal::add)
                ));
        //员工最大工时
        BigDecimal maxStandardTimeValue = employeeStandardTimeSumMap.values().stream()
                .max(BigDecimal::compareTo).get();
        //所以工时的和
        BigDecimal sumOfStandardTime = apsProcessSchemeParam.stream()
                .map(ApsProcessSchemeParam::getStandardTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //产线平衡率
        BigDecimal productionLineBalanceRate = sumOfStandardTime.divide(maxStandardTimeValue.multiply(new BigDecimal(number)), 8, RoundingMode.HALF_UP);
        //可以释放人数
        long countEqualMaxStandardTime = employeeStandardTimeSumMap.values().stream()
                .filter(value -> value.compareTo(maxStandardTimeValue) == 0)
                .count();
        long releasableStaffCount = number - countEqualMaxStandardTime;
        apsProductFamilyProcessSchemeManagement.setProductionLineBalanceRate(productionLineBalanceRate);
        apsProductFamilyProcessSchemeManagement.setReleasableStaffCount((int) releasableStaffCount);

        if (CollectionUtils.isEmpty(apsProductFamilyProcessSchemeManagements)) {
            //没有设置经济批量直接保存 // 没有其他最优方案 那么本身就是最优方案
            apsProductFamilyProcessSchemeManagement.setOptimalProcessSchemeName(currentProcessScheme);
            apsProductFamilyProcessSchemeManagementMapper.insert(apsProductFamilyProcessSchemeManagement);
        } else {
            String optimalName = null;
            //只要有数据就要判断最优方案
            //所有的数据的最优方案都是一个
            ApsProductFamilyProcessSchemeManagement apsProductManagementOptimalToName = apsProductFamilyProcessSchemeManagements.get(0);
            List<ApsProcessSchemeVo> apsProcessSchemeVoList = apsProcessSchemeMapper.selectProcessSchemeBycurrentProcessScheme(apsProductManagementOptimalToName.getOptimalProcessSchemeName());
            //最优每个员工的map和工时和
            Map<String, BigDecimal> employeeStandardTimeSumMapOptimal = apsProcessSchemeVoList.stream()
                    .collect(Collectors.groupingBy(
                            ApsProcessSchemeVo::getEmployeeName,
                            Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeVo::getStandardTime, BigDecimal::add)
                    ));
            //最优的员工最大工时
            BigDecimal maxStandardTimeValueOptimal = employeeStandardTimeSumMapOptimal.values().stream()
                    .max(BigDecimal::compareTo).get();
            //最优工时的和
            BigDecimal sumOfStandardTimeOptimal = apsProcessSchemeVoList.stream()
                    .map(ApsProcessSchemeVo::getStandardTime)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            //最优的产线平衡率
            BigDecimal productionLineBalanceRateOptimal = sumOfStandardTimeOptimal.divide(maxStandardTimeValueOptimal.multiply(new BigDecimal(number)), 8, RoundingMode.HALF_UP);
            LambdaQueryWrapper<ApsOptimalStrategy> apsOptimalStrategyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apsOptimalStrategyLambdaQueryWrapper.eq(ApsOptimalStrategy::getNumber, number).eq(ApsOptimalStrategy::getProductFamily, productFamily);
            ApsOptimalStrategy one = apsOptimalStrategyService.getOne(apsOptimalStrategyLambdaQueryWrapper);
            Integer strategy = one.getStrategy();
            if (strategy == 1) {
                //最优的时间比 新添加的还要长那么就要更新
                if (maxStandardTimeValueOptimal.compareTo(maxStandardTimeValue) > 0) {
                    optimalName = currentProcessScheme;
                }
            } else {
                //平衡率越大越好
                if (productionLineBalanceRateOptimal.compareTo(productionLineBalanceRate) < 0) {
                    optimalName = currentProcessScheme;
                }
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(optimalName)) {
                String finalOptimalName = optimalName;
                apsProductFamilyProcessSchemeManagements = apsProductFamilyProcessSchemeManagements.stream()
                        .peek(x -> x.setOptimalProcessSchemeName(finalOptimalName)).collect(Collectors.toList());
                apsProductFamilyProcessSchemeManagementService.updateBatchById(apsProductFamilyProcessSchemeManagements);
                apsProductFamilyProcessSchemeManagement.setOptimalProcessSchemeName(optimalName);
            } else {
                apsProductFamilyProcessSchemeManagement.setOptimalProcessSchemeName(apsProductManagementOptimalToName.getOptimalProcessSchemeName());
            }
            if (apsProductFamilyProcessSchemeManagements.get(0).getOrderNumber() != null) {
                //如果有数据 有经济批量那么就要计算
                Integer orderNumber = apsProductFamilyProcessSchemeManagements.get(0).getOrderNumber();
                //订单完成时间
                BigDecimal completionTime = maxStandardTimeValue.multiply(new BigDecimal(orderNumber));

                //释放总时间
                BigDecimal differenceSum = employeeStandardTimeSumMap.values().stream()
                        .map(maxStandardTimeValue::subtract)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                differenceSum = differenceSum.multiply(new BigDecimal(orderNumber));
                apsProductFamilyProcessSchemeManagement.setOrderNumber(orderNumber);
                apsProductFamilyProcessSchemeManagement.setCompletionTime(completionTime);
                apsProductFamilyProcessSchemeManagement.setTotalReleaseTime(differenceSum.doubleValue());
            }
            apsProductFamilyProcessSchemeManagementMapper.insert(apsProductFamilyProcessSchemeManagement);
        }

    }


    @Override
    public ApsProcessSchemeVoPage getProcessScheme(Integer page, Integer size) {
        Integer pass = (page - 1) * size;
        List<ApsProcessSchemeVo> apsProcessSchemeVoList = apsProcessSchemeMapper.selectProcessSchemePage(pass, size);
        ApsProcessSchemeVoPage apsProcessSchemeVoPage = new ApsProcessSchemeVoPage();
        apsProcessSchemeVoPage.setApsProcessSchemeVo(apsProcessSchemeVoList);
        apsProcessSchemeVoPage.setPage(page);
        apsProcessSchemeVoPage.setSize(size);
        Long total = apsProcessSchemeMapper.selectCount(null);
        apsProcessSchemeVoPage.setTotal(total);
        apsProcessSchemeVoPage.setPages(total / size + 1);
        return apsProcessSchemeVoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProcessScheme(List<Integer> ids) {
        List<ApsProcessScheme> apsProcessSchemes = getApsProcessSchemes(ids);
        List<String> currentProcessSchemeList = apsProcessSchemes.stream().map(ApsProcessScheme::getCurrentProcessScheme).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentProcessSchemeList)) {
            return true;
        }
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProductFamilyProcessSchemeManagementLambdaQueryWrapper.in(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName, currentProcessSchemeList);
        List<ApsProductFamilyProcessSchemeManagement> apsProductManagements = apsProductFamilyProcessSchemeManagementMapper.selectList(apsProductFamilyProcessSchemeManagementLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(apsProductManagements)) {
            Map<String, List<ApsProductFamilyProcessSchemeManagement>> optimalMap = apsProductManagements.stream()
                    .collect(Collectors.groupingBy(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName));
            for (Map.Entry<String, List<ApsProductFamilyProcessSchemeManagement>> apsProductManList : optimalMap.entrySet()) {
                BigDecimal maxStandardTime = new BigDecimal(Double.MAX_VALUE);
                BigDecimal maxProductionLineBalanceRate = new BigDecimal(Double.MIN_VALUE);
                String optimalName = null;
                List<ApsProductFamilyProcessSchemeManagement> value = apsProductManList.getValue();
                ApsOptimalStrategy apsOptimalStrategyServiceOne = apsOptimalStrategyService.getOne(new LambdaQueryWrapper<ApsOptimalStrategy>()
                        .eq(ApsOptimalStrategy::getProductFamily, value.get(0).getProductFamily())
                        .eq(ApsOptimalStrategy::getNumber, value.get(0).getNumber()));
                for (ApsProductFamilyProcessSchemeManagement apsProductManagementItme : value) {
                    if (!apsProductManagementItme.getCurProcessSchemeName().equals(apsProductManList.getKey())) {
                        List<ApsProcessSchemeVo> apsProcessSchemeVoList = apsProcessSchemeMapper
                                .selectProcessSchemeBycurrentProcessScheme(apsProductManagementItme.getCurProcessSchemeName());
                        //最优每个员工的map和工时和
                        Map<String, BigDecimal> employeeStandardTimeSumMap = apsProcessSchemeVoList.stream()
                                .collect(Collectors.groupingBy(
                                        ApsProcessSchemeVo::getEmployeeName,
                                        Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeVo::getStandardTime, BigDecimal::add)
                                ));
                        //最优的员工最大工时
                        BigDecimal maxStandardTimeValue = employeeStandardTimeSumMap.values().stream()
                                .max(BigDecimal::compareTo).get();
                        //最优工时的和
                        BigDecimal sumOfStandardTimeOptimal = apsProcessSchemeVoList.stream()
                                .map(ApsProcessSchemeVo::getStandardTime)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        //最优的产线平衡率
                        BigDecimal productionLineBalanceRateOptimal = sumOfStandardTimeOptimal
                                .divide(maxStandardTimeValue.multiply(new BigDecimal(apsProcessSchemeVoList.get(0).getNumber())), 8, RoundingMode.HALF_UP);
                        if (apsOptimalStrategyServiceOne.getStrategy() == 2) {
                            if (productionLineBalanceRateOptimal.compareTo(maxProductionLineBalanceRate) > 0) {
                                maxProductionLineBalanceRate = productionLineBalanceRateOptimal;
                                optimalName = apsProductManagementItme.getCurProcessSchemeName();
                            }
                        } else {
                            if (maxStandardTimeValue.compareTo(maxStandardTime) < 0) {
                                maxStandardTime = maxStandardTimeValue;
                                optimalName = apsProductManagementItme.getCurProcessSchemeName();
                            }
                        }
                    }
                    String finalOptimalName = optimalName;
                    List<ApsProductFamilyProcessSchemeManagement> updateProductFamilyProcessSchemeManagements = value.stream().peek(x -> x.setOptimalProcessSchemeName(finalOptimalName)).collect(Collectors.toList());
                    apsProductFamilyProcessSchemeManagementService.updateBatchById(updateProductFamilyProcessSchemeManagements);

                }
            }
        }
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProductQueryWrapper = new LambdaQueryWrapper<>();
        apsProductQueryWrapper.eq(ApsProductFamilyProcessSchemeManagement::getCurProcessSchemeName ,currentProcessSchemeList.get(0));
        apsProductFamilyProcessSchemeManagementService.remove(apsProductQueryWrapper);
        LambdaQueryWrapper<ApsProcessScheme> apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeLambdaQueryWrapper.in(ApsProcessScheme::getCurrentProcessScheme, currentProcessSchemeList);
        return remove(apsProcessSchemeLambdaQueryWrapper);
    }

    //根据id查询
    private List<ApsProcessScheme> getApsProcessSchemes(List<Integer> ids) {
        List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeMapper.selectListByIds(ids);
        return apsProcessSchemes;
    }

    @Override
    public ApsProcessSchemeByIdListVo getProcessSchemeById(Integer id) {
        ApsProcessScheme apsProcessScheme = getById(id);
        if (apsProcessScheme != null && StringUtils.isNotEmpty(apsProcessScheme.getCurrentProcessScheme())) {
            List<ApsProcessSchemeVo> apsProcessSchemeVoList = apsProcessSchemeMapper.selectProcessSchemeBycurrentProcessScheme(apsProcessScheme.getCurrentProcessScheme());
            long size = apsProcessSchemeVoList.stream().map(ApsProcessSchemeVo::getProductFamily).distinct().count();
            if (size != 1) {
                throw new BeneWakeException("当前数据有问题！");
            }
            ApsProcessSchemeByIdListVo apsProcessSchemeByIdListVo = new ApsProcessSchemeByIdListVo();
            apsProcessSchemeByIdListVo.setApsProcessSchemeVoList(apsProcessSchemeVoList);
            String productFamily = apsProcessSchemeVoList.get(0).getProductFamily();
            apsProcessSchemeByIdListVo.setProductFamily(productFamily);
            apsProcessSchemeByIdListVo.setNumber(apsProcessSchemeVoList.get(0).getNumber());
            return apsProcessSchemeByIdListVo;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateProcessScheme(List<ApsProcessSchemeParam> apsProcessSchemeParam) {
        String productFamily = apsProcessSchemeParam.get(0).getProductFamily();
        List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeParam.stream().map(x -> {
            ApsProcessScheme apsProcessScheme = new ApsProcessScheme();
            apsProcessScheme.setId(x.getId());
            apsProcessScheme.setEmployeeName(x.getEmployeeName());
            return apsProcessScheme;
        }).collect(Collectors.toList());

        long size = apsProcessSchemeParam.stream().map(ApsProcessSchemeParam::getEmployeeName).distinct().count();
        LambdaQueryWrapper<ApsProcessScheme> apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeLambdaQueryWrapper.eq(ApsProcessScheme::getId, apsProcessSchemeParam.get(0).getId());
        ApsProcessScheme one = getOne(apsProcessSchemeLambdaQueryWrapper);
        if (one == null) {
            throw new BeneWakeException("当前数据有问题");
        }
        Integer number = one.getNumber();
        if (size != number) {
            throw new BeneWakeException("请按照输入人数分组");
        }
        //todo 重新计算最优
        Integer id = apsProcessSchemeParam.get(0).getId();
        ApsProcessScheme processScheme = getById(id);
        String currentProcessScheme = processScheme.getCurrentProcessScheme();
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProductFamilyProcessSchemeManagementLambdaQueryWrapper.eq(ApsProductFamilyProcessSchemeManagement::getCurProcessSchemeName, currentProcessScheme);
        ApsProductFamilyProcessSchemeManagement curApsProductMana = apsProductFamilyProcessSchemeManagementService.getOne(apsProductFamilyProcessSchemeManagementLambdaQueryWrapper);
        String optimalProcessSchemeName = curApsProductMana.getOptimalProcessSchemeName();
        List<ApsProcessSchemeVo> apsProcessSchemeVoListOptimal = apsProcessSchemeMapper.selectProcessSchemeBycurrentProcessScheme(optimalProcessSchemeName);

        //最优每个员工的map和工时和
        Map<String, BigDecimal> employeeStandardTimeSumMap = apsProcessSchemeVoListOptimal.stream()
                .collect(Collectors.groupingBy(
                        ApsProcessSchemeVo::getEmployeeName,
                        Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeVo::getStandardTime, BigDecimal::add)
                ));
        //最优的员工最大工时
        BigDecimal maxStandardTimeValue = employeeStandardTimeSumMap.values().stream()
                .max(BigDecimal::compareTo).get();
        //最优工时的和
        BigDecimal sumOfStandardTimeOptimal = apsProcessSchemeVoListOptimal.stream()
                .map(ApsProcessSchemeVo::getStandardTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //更新的产线平衡率
        BigDecimal productionLineBalanceRateOptimal = sumOfStandardTimeOptimal
                .divide(maxStandardTimeValue.multiply(new BigDecimal(apsProcessSchemeVoListOptimal.get(0).getNumber())), 8, RoundingMode.HALF_UP);
        //---------------------
        //更新每个员工的map和工时和
        Map<String, BigDecimal> employeeStandardTimeSum = apsProcessSchemeParam.stream()
                .collect(Collectors.groupingBy(
                        ApsProcessSchemeParam::getEmployeeName,
                        Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeParam::getStandardTime, BigDecimal::add)
                ));
        BigDecimal maxStandardTime = employeeStandardTimeSum.values().stream()
                .max(BigDecimal::compareTo).get();
        BigDecimal sumOfStandardTime = apsProcessSchemeVoListOptimal.stream()
                .map(ApsProcessSchemeVo::getStandardTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal productionLineBalanceRate = sumOfStandardTimeOptimal
                .divide(sumOfStandardTime.multiply(new BigDecimal(apsProcessSchemeVoListOptimal.get(0).getNumber())), 8, RoundingMode.HALF_UP);

        LambdaQueryWrapper<ApsOptimalStrategy> apsOptimalStrategyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsOptimalStrategyLambdaQueryWrapper.eq(ApsOptimalStrategy::getProductFamily, productFamily)
                .eq(ApsOptimalStrategy::getNumber, number);
        ApsOptimalStrategy apsOptimalStrategyServiceOne = apsOptimalStrategyService.getOne(apsOptimalStrategyLambdaQueryWrapper);
        Integer strategy = apsOptimalStrategyServiceOne.getStrategy();
        LambdaUpdateWrapper<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagementLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        apsProductFamilyProcessSchemeManagementLambdaUpdateWrapper.eq(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName, optimalProcessSchemeName)
                .set(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName, currentProcessScheme);
        if (strategy == 2) {
            if (productionLineBalanceRateOptimal.compareTo(productionLineBalanceRate) > 0) {
                apsProductFamilyProcessSchemeManagementService.update(apsProductFamilyProcessSchemeManagementLambdaUpdateWrapper);
            }
        } else {
            if (maxStandardTimeValue.compareTo(maxStandardTime) < 0) {
                apsProductFamilyProcessSchemeManagementService.update(apsProductFamilyProcessSchemeManagementLambdaUpdateWrapper);
            }
        }

        Integer processId = apsProcessSchemeParam.get(0).getId();
        ApsProcessScheme apsProcessScheme = apsProcessSchemeMapper.selectById(processId);
        saveProducSchemeManagement(apsProcessSchemeParam, number, apsProcessScheme.getCurrentProcessScheme());
        if (updateBatchById(apsProcessSchemes)) {
            return apsProcessSchemes.get(0).getCurrentProcessScheme();
        }
        return null;
    }
}




