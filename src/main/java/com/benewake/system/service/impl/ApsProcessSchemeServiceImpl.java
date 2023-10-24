package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessScheme;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.entity.vo.ApsProcessSchemeByIdListVo;
import com.benewake.system.entity.vo.ApsProcessSchemeParam;
import com.benewake.system.entity.vo.ApsProcessSchemeParams;
import com.benewake.system.entity.vo.ApsProcessSchemeVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.mapper.ApsProductFamilyProcessSchemeManagementMapper;
import com.benewake.system.service.ApsProcessSchemeService;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                && org.apache.commons.lang3.StringUtils.isNotBlank(processSchemes.get(0))) {
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
        if (!saveBatch(apsProcessSchemes)) {
            throw new BeneWakeException("保存失败");
        }
        return currentProcessScheme;
    }

    private void saveProducSchemeManagement(List<ApsProcessSchemeParam> apsProcessSchemeParam, Integer number, String currentProcessScheme) {
        //填充最优表
        String productFamily = apsProcessSchemeParam.get(0).getProductFamily();
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProcessSchemeManagementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeManagementLambdaQueryWrapper.eq(ApsProductFamilyProcessSchemeManagement::getProductFamily, number)
                .eq(ApsProductFamilyProcessSchemeManagement::getProductFamily ,productFamily);
        List<ApsProductFamilyProcessSchemeManagement> apsProductFamilyProcessSchemeManagements = apsProductFamilyProcessSchemeManagementMapper.selectList(apsProcessSchemeManagementLambdaQueryWrapper);
        ApsProductFamilyProcessSchemeManagement apsProductFamilyProcessSchemeManagement = new ApsProductFamilyProcessSchemeManagement();
        apsProductFamilyProcessSchemeManagement.setCurProcessSchemeName(currentProcessScheme);
        apsProductFamilyProcessSchemeManagement.setNumber(number);
        apsProductFamilyProcessSchemeManagement.setProductFamily(productFamily);
        if (CollectionUtils.isEmpty(apsProductFamilyProcessSchemeManagements) || apsProductFamilyProcessSchemeManagements.get(0).getOrderNumber() == null) {
            apsProductFamilyProcessSchemeManagementMapper.insert(apsProductFamilyProcessSchemeManagement);
        } else {
            Integer orderNumber = apsProductFamilyProcessSchemeManagements.get(0).getOrderNumber();
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
            BigDecimal multiply = maxStandardTimeValue.multiply(new BigDecimal(number));
            //产线平衡率
            BigDecimal productionLineBalanceRate = sumOfStandardTime.subtract(multiply);
            //订单完成时间
            BigDecimal completionTime = maxStandardTimeValue.multiply(new BigDecimal(orderNumber));
            long countEqualMaxStandardTime = employeeStandardTimeSumMap.values().stream()
                    .filter(value -> value.compareTo(maxStandardTimeValue) == 0)
                    .count();
            //可以释放人数
            long releasableStaffCount = number - countEqualMaxStandardTime;
            //总是释放时间
            BigDecimal sum = new BigDecimal(0);
            for (Map.Entry<String, BigDecimal> stringBigDecimalEntry : employeeStandardTimeSumMap.entrySet()) {
                BigDecimal subtract = maxStandardTimeValue.subtract(stringBigDecimalEntry.getValue());
                sum = sum.add(subtract);
            }
            //todo 最优
            sum = sum.multiply(new BigDecimal(orderNumber));
            apsProductFamilyProcessSchemeManagement.setOrderNumber(orderNumber);
            apsProductFamilyProcessSchemeManagement.setProductionLineBalanceRate(productionLineBalanceRate);
            apsProductFamilyProcessSchemeManagement.setCompletionTime(completionTime);
            apsProductFamilyProcessSchemeManagement.setReleasableStaffCount((int) releasableStaffCount);
            apsProductFamilyProcessSchemeManagement.setTotalReleaseTime(sum.doubleValue());
            apsProductFamilyProcessSchemeManagementMapper.insert(apsProductFamilyProcessSchemeManagement);
        }
    }

    @Override
    public List<ApsProcessSchemeVo> getProcessScheme(Integer page, Integer size) {
        Integer pass = (page - 1) * size;
        return apsProcessSchemeMapper.selectProcessSchemePage(pass, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProcessScheme(List<Integer> ids) {
        //todo 重新计算最优
        List<ApsProcessScheme> apsProcessSchemes = getApsProcessSchemes(ids);
        LambdaQueryWrapper<ApsProcessScheme> apsProcessSchemeLambdaQueryWrapper;
        List<String> currentProcessSchemeList = apsProcessSchemes.stream().map(ApsProcessScheme::getCurrentProcessScheme).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentProcessSchemeList)) {
            return true;
        }
        apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
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
        ApsProcessScheme apsProcessScheme = apsProcessSchemeMapper.selectById(apsProcessSchemeParam.get(0).getProcessId());
        saveProducSchemeManagement(apsProcessSchemeParam ,number ,apsProcessScheme.getCurrentProcessScheme());
        if (updateBatchById(apsProcessSchemes)) {
            return apsProcessSchemes.get(0).getCurrentProcessScheme();
        }
        return null;
    }
}




