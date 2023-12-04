package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOptimalStrategy;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.entity.dto.ApsProcessSchemeDto;
import com.benewake.system.entity.vo.UpdateOptimalStrategyParam;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsOptimalStrategyMapper;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.service.ApsOptimalStrategyService;
import com.benewake.system.service.ApsProductFamilyProcessSchemeManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ASUS
 * @description 针对表【aps_optimal_strategy】的数据库操作Service实现
 * @createDate 2023-10-24 21:02:26
 */
@Service
public class ApsOptimalStrategyServiceImpl extends ServiceImpl<ApsOptimalStrategyMapper, ApsOptimalStrategy>
        implements ApsOptimalStrategyService {

    @Autowired
    private ApsOptimalStrategyMapper apsOptimalStrategyMapper;

    @Autowired
    private ApsProductFamilyProcessSchemeManagementService apsProductFamilyProcessSchemeManagementService;


    @Autowired
    private ApsProcessSchemeMapper apsProcessSchemeMapper;

    @Override
    public void ifInsert(ApsOptimalStrategy apsOptimalStrategy) {
        LambdaQueryWrapper<ApsOptimalStrategy> apsOptimalStrategyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsOptimalStrategyLambdaQueryWrapper
                .eq(ApsOptimalStrategy::getProductFamily, apsOptimalStrategy.getProductFamily())
                .eq(ApsOptimalStrategy::getNumber, apsOptimalStrategy.getNumber());
        ApsOptimalStrategy apsOptimalStrategyByDate = apsOptimalStrategyMapper.selectOne(apsOptimalStrategyLambdaQueryWrapper);
        if (apsOptimalStrategyByDate == null) {
            save(apsOptimalStrategy);
        }
    }

    @Override
    public Boolean updateAndOptimalProcess(UpdateOptimalStrategyParam updateOptimalStrategyParam) {
        //查出当前产品族下 所有方案
        //便利当前方案 查出最大时间和平衡率 记录下名字 然后更新
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProductManagementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProductManagementLambdaQueryWrapper.eq(ApsProductFamilyProcessSchemeManagement::getProductFamily, updateOptimalStrategyParam.getProductFamily())
                .eq(ApsProductFamilyProcessSchemeManagement::getNumber, updateOptimalStrategyParam.getNumber());
        List<ApsProductFamilyProcessSchemeManagement> apsProductManList = apsProductFamilyProcessSchemeManagementService.getBaseMapper().selectList(apsProductManagementLambdaQueryWrapper);

        if (apsProductManList == null) {
            throw new BeneWakeException("当前没有最终工艺方案~");
        }

        BigDecimal maxStandardTimeValueOptimal = new BigDecimal(Double.MAX_VALUE);
        BigDecimal productionLineBalanceRateOptimal = new BigDecimal(Double.MIN_VALUE);
        String optimalName = null;
        for (ApsProductFamilyProcessSchemeManagement itme : apsProductManList) {
            List<ApsProcessSchemeDto> apsProcessSchemeDtoList = apsProcessSchemeMapper.selectProcessSchemeBycurrentProcessScheme(itme.getCurProcessSchemeName());
            //最优每个员工的map和工时和
            Map<String, BigDecimal> employeeStandardTimeSumMapOptimal = apsProcessSchemeDtoList.stream()
                    .collect(Collectors.groupingBy(
                            ApsProcessSchemeDto::getEmployeeName,
                            Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeDto::getStandardTime, BigDecimal::add)
                    ));
            //最优的员工最大工时
            BigDecimal standardTimeValueOptimal = employeeStandardTimeSumMapOptimal.values().stream()
                    .max(BigDecimal::compareTo).get();
            //最优工时的和
            BigDecimal sumOfStandardTime = apsProcessSchemeDtoList.stream()
                    .map(ApsProcessSchemeDto::getStandardTime)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            //最优的产线平衡率
            BigDecimal productionLineBalanceRate = sumOfStandardTime
                    .divide(standardTimeValueOptimal.multiply(new BigDecimal(updateOptimalStrategyParam.getNumber())), 8, RoundingMode.HALF_UP);
            if (updateOptimalStrategyParam.getStrategy() == 1) {
                if (maxStandardTimeValueOptimal.compareTo(standardTimeValueOptimal) > 0) {
                    maxStandardTimeValueOptimal = standardTimeValueOptimal;
                    optimalName = itme.getCurProcessSchemeName();
                }
            }else {
                if (productionLineBalanceRateOptimal.compareTo(productionLineBalanceRate) < 0) {
                    productionLineBalanceRateOptimal = productionLineBalanceRate;
                    optimalName = itme.getCurProcessSchemeName();
                }
            }

        }
        String finalOptimalName = optimalName;
        apsProductManList = apsProductManList.stream().peek(x -> {
            x.setOptimalProcessSchemeName(finalOptimalName);
        }).collect(Collectors.toList());
        apsProductFamilyProcessSchemeManagementService.updateBatchById(apsProductManList);
        LambdaQueryWrapper<ApsOptimalStrategy> apsOptimalStrategyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsOptimalStrategyLambdaQueryWrapper.eq(ApsOptimalStrategy::getStrategy ,updateOptimalStrategyParam.getStrategy())
                        .eq(ApsOptimalStrategy::getNumber ,updateOptimalStrategyParam.getNumber());
        return update(apsOptimalStrategyLambdaQueryWrapper);
    }

}




