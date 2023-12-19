package com.benewake.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsOptimalStrategy;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessScheme;
import com.benewake.system.entity.ApsProductFamilyProcessSchemeManagement;
import com.benewake.system.entity.dto.ApsProcessSchemeDto;
import com.benewake.system.entity.enums.ExcelOperationEnum;
import com.benewake.system.entity.vo.*;
import com.benewake.system.excel.entity.ExcelProcessScheme;
import com.benewake.system.excel.transfer.ProcessSchemeVoToExcelList;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.mapper.ApsProductFamilyProcessSchemeManagementMapper;
import com.benewake.system.service.ApsOptimalStrategyService;
import com.benewake.system.service.ApsProcessSchemeService;
import com.benewake.system.service.ApsProductFamilyProcessSchemeManagementService;
import com.benewake.system.transfer.ApsProcessSchemeDtoToVo;
import com.benewake.system.utils.BenewakeStringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
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
    private ApsProductFamilyProcessSchemeManagementMapper apsProductManagements;

    @Autowired
    private ApsOptimalStrategyService apsOptimalStrategyService;

    @Autowired
    private ApsProductFamilyProcessSchemeManagementService apsProductFamilyProcessSchemeManagementService;

    @Autowired
    private ApsProcessSchemeDtoToVo apsProcessSchemeDtoToVo;

    @Autowired
    private ProcessSchemeVoToExcelList processSchemeVoToExcelList;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized String saveProcessScheme(ApsProcessSchemeParams apsProcessSchemeParams) {
        List<ApsProcessSchemeParam> apsProcessSchemeParam = apsProcessSchemeParams.getApsProcessSchemeParam();
        List<String> employeeList = apsProcessSchemeParam.stream().map(ApsProcessSchemeParam::getEmployeeName).distinct().collect(Collectors.toList());
        int size = employeeList.size();
        if (size != apsProcessSchemeParams.getNumber()) {
            throw new BeneWakeException("输入人数 与分配人数 不一致~~~~~~~");
        }
        //todo 判断是否cunzai当前新增的方案
//        apsProcessSchemeParam.stream()
        List<Integer> processCapacityIds = apsProcessSchemeParam.stream()
                .map(ApsProcessSchemeParam::getId)
                .collect(Collectors.toList());
        Integer number = apsProcessSchemeParams.getNumber();
        String currentProcessScheme;
        List<String> processSchemes = apsProcessSchemeMapper.selectSchemeBycaIdandNumber(processCapacityIds, number);
        if (CollectionUtils.isNotEmpty(processSchemes)
                && BenewakeStringUtils.isNotBlank(processSchemes.get(0))) {
            currentProcessScheme = BenewakeStringUtils.incrementAndExtractLast(processSchemes);
        } else {
            ApsProcessCapacity apsProcessCapacity = apsProcessCapacityMapper
                    .selectOne(new LambdaQueryWrapper<ApsProcessCapacity>()
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
            apsProcessScheme.setState(true);
            return apsProcessScheme;
        }).collect(Collectors.toList());
        saveProducSchemeManagement(apsProcessSchemeParam, number, currentProcessScheme);
        ApsProcessCapacity apsProcessCapacity = apsProcessCapacityMapper.selectById(processCapacityIds.get(0));

        //保存优先级方案
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
                apsProductManagements.selectList(apsProcessSchemeManagementLambdaQueryWrapper);
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
            apsProductManagements.insert(apsProductFamilyProcessSchemeManagement);
        } else {
            String optimalName = null;
            //只要有数据就要判断最优方案
            //所有的数据的最优方案都是一个
            ApsProductFamilyProcessSchemeManagement apsProductManagementOptimalToName = apsProductFamilyProcessSchemeManagements.get(0);
            List<ApsProcessSchemeDto> apsProcessSchemeDtoList = apsProcessSchemeMapper.selectProcessSchemeBycurrentProcessScheme(apsProductManagementOptimalToName.getOptimalProcessSchemeName());
            //最优每个员工的map和工时和
            Map<String, BigDecimal> employeeStandardTimeSumMapOptimal = apsProcessSchemeDtoList.stream()
                    .collect(Collectors.groupingBy(
                            ApsProcessSchemeDto::getEmployeeName,
                            Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeDto::getStandardTime, BigDecimal::add)
                    ));
            //最优的员工最大工时
            BigDecimal maxStandardTimeValueOptimal = employeeStandardTimeSumMapOptimal.values().stream()
                    .max(BigDecimal::compareTo).get();
            //最优工时的和
            BigDecimal sumOfStandardTimeOptimal = apsProcessSchemeDtoList.stream()
                    .map(ApsProcessSchemeDto::getStandardTime)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            //最优的产线平衡率
            BigDecimal productionLineBalanceRateOptimal = sumOfStandardTimeOptimal.divide(maxStandardTimeValueOptimal.multiply(new BigDecimal(number)), 8, RoundingMode.HALF_UP);
            LambdaQueryWrapper<ApsOptimalStrategy> apsOptimalStrategyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apsOptimalStrategyLambdaQueryWrapper.eq(ApsOptimalStrategy::getNumber, number).eq(ApsOptimalStrategy::getProductFamily, productFamily);
            ApsOptimalStrategy one = apsOptimalStrategyService.getOne(apsOptimalStrategyLambdaQueryWrapper);
            Integer strategy = one != null ? one.getStrategy() : 1;
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
                //将所有最优的都设置成刚刚判断出来的结果
                apsProductFamilyProcessSchemeManagements = apsProductFamilyProcessSchemeManagements.stream()
                        .peek(x -> x.setOptimalProcessSchemeName(finalOptimalName)).collect(Collectors.toList());
                apsProductFamilyProcessSchemeManagementService.updateBatchById(apsProductFamilyProcessSchemeManagements);
                //设置自己
                apsProductFamilyProcessSchemeManagement.setOptimalProcessSchemeName(optimalName);
            } else {
                //如果是null说明 新增的不如原来的方案 那么他的最优方案还是他自己
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
            apsProductManagements.insert(apsProductFamilyProcessSchemeManagement);
        }

    }


    @Override
    public ApsProcessSchemeVoPage getProcessScheme(Integer page, Integer size) {
        Page<ApsProcessScheme> schemePage = new Page<>();
        schemePage.setCurrent(page);
        schemePage.setSize(size);
        Page<ApsProcessSchemeDto> apsProcessSchemeVoList = apsProcessSchemeMapper.selectProcessSchemePage(schemePage);
        List<ApsProcessSchemeDto> records = apsProcessSchemeVoList.getRecords();
        List<ApsProcessSchemeVo> apsProcessSchemeVos = records.stream().map(x -> apsProcessSchemeDtoToVo.convert(x))
                .collect(Collectors.toList());
        ApsProcessSchemeVoPage apsProcessSchemeVoPage = new ApsProcessSchemeVoPage();
        apsProcessSchemeVoPage.setApsProcessSchemeVos(apsProcessSchemeVos);
        apsProcessSchemeVoPage.setPage(page);
        apsProcessSchemeVoPage.setSize(size);
        apsProcessSchemeVoPage.setTotal(apsProcessSchemeVoList.getTotal());
        apsProcessSchemeVoPage.setPages(apsProcessSchemeVoList.getPages());
        return apsProcessSchemeVoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProcessScheme(List<Integer> ids) {
        //获取所有的工艺方案信息
        List<ApsProcessScheme> apsProcessSchemes = getApsProcessSchemes(ids);
        List<String> currentProcessSchemeList = apsProcessSchemes.stream()
                .map(ApsProcessScheme::getCurrentProcessScheme).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentProcessSchemeList)) {
            return true;
        }
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> managementLambdaQueryWrapper = new LambdaQueryWrapper<>();
        managementLambdaQueryWrapper.in(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName, currentProcessSchemeList);
        //查出如果这些删除的有最优方案那么就需要重新计算咯
        List<ApsProductFamilyProcessSchemeManagement> apsProductManagements = this.apsProductManagements.selectList(managementLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(apsProductManagements)) {
            Map<String, List<ApsProductFamilyProcessSchemeManagement>> optimalMap = apsProductManagements.stream()
                    .collect(Collectors.groupingBy(ApsProductFamilyProcessSchemeManagement::getOptimalProcessSchemeName));
            for (Map.Entry<String, List<ApsProductFamilyProcessSchemeManagement>> apsProductManList : optimalMap.entrySet()) {
                BigDecimal maxStandardTime = new BigDecimal(Double.MAX_VALUE);
                BigDecimal maxProductionLineBalanceRate = new BigDecimal(Double.MIN_VALUE);
                String optimalName = null;
                List<ApsProductFamilyProcessSchemeManagement> value = apsProductManList.getValue();
                //获取最优方案选择策略
                ApsOptimalStrategy apsOptimalStrategyServiceOne = apsOptimalStrategyService.getOne(new LambdaQueryWrapper<ApsOptimalStrategy>()
                        .eq(ApsOptimalStrategy::getProductFamily, value.get(0).getProductFamily())
                        .eq(ApsOptimalStrategy::getNumber, value.get(0).getNumber()));
                for (ApsProductFamilyProcessSchemeManagement apsProductManagementItme : value) {
                    if (!apsProductManagementItme.getCurProcessSchemeName().equals(apsProductManList.getKey())) {
                        List<ApsProcessSchemeDto> apsProcessSchemeDtoList = apsProcessSchemeMapper
                                .selectProcessSchemeBycurrentProcessScheme(apsProductManagementItme.getCurProcessSchemeName());
                        //最优每个员工的map和工时和
                        Map<String, BigDecimal> employeeStandardTimeSumMap = apsProcessSchemeDtoList.stream()
                                .collect(Collectors.groupingBy(
                                        ApsProcessSchemeDto::getEmployeeName,
                                        Collectors.reducing(BigDecimal.ZERO, ApsProcessSchemeDto::getStandardTime, BigDecimal::add)
                                ));
                        //最优的员工最大工时
                        BigDecimal maxStandardTimeValue = employeeStandardTimeSumMap.values().stream()
                                .max(BigDecimal::compareTo).get();
                        //最优工时的和
                        BigDecimal sumOfStandardTimeOptimal = apsProcessSchemeDtoList.stream()
                                .map(ApsProcessSchemeDto::getStandardTime)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        //最优的产线平衡率
                        BigDecimal productionLineBalanceRateOptimal = sumOfStandardTimeOptimal
                                .divide(maxStandardTimeValue.multiply(new BigDecimal(apsProcessSchemeDtoList.get(0).getNumber())), 8, RoundingMode.HALF_UP);
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
        //删除记录
        LambdaQueryWrapper<ApsProductFamilyProcessSchemeManagement> apsProductQueryWrapper = new LambdaQueryWrapper<>();
        apsProductQueryWrapper.in(ApsProductFamilyProcessSchemeManagement::getCurProcessSchemeName, currentProcessSchemeList);
        apsProductFamilyProcessSchemeManagementService.remove(apsProductQueryWrapper);
        //删除
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
        if (apsProcessScheme == null || BenewakeStringUtils.isEmpty(apsProcessScheme.getCurrentProcessScheme())) {
            return null;
        }
        ApsProcessCapacity processCapacity = apsProcessCapacityMapper.selectById(apsProcessScheme.getProcessCapacityId());
        String productFamily = processCapacity.getProductFamily();
        List<ApsProcessSchemeDto> apsProcessSchemeDtoList = apsProcessSchemeMapper
                .selectProcessSchemeByProcessScheme(apsProcessScheme.getCurrentProcessScheme(), productFamily);
        long size = apsProcessSchemeDtoList.stream().map(ApsProcessSchemeDto::getProductFamily).distinct().count();
        if (size != 1) {
            throw new BeneWakeException("当前数据有问题！");
        }
        List<ApsProcessSchemeVo> apsProcessSchemeVos = apsProcessSchemeDtoList.stream()
                .map(x -> apsProcessSchemeDtoToVo.convert(x))
                .collect(Collectors.toList());
        ApsProcessSchemeByIdListVo apsProcessSchemeByIdListVo = new ApsProcessSchemeByIdListVo();
        apsProcessSchemeByIdListVo.setApsProcessSchemeVoList(apsProcessSchemeVos);
        apsProcessSchemeByIdListVo.setProductFamily(productFamily);
        apsProcessSchemeByIdListVo.setNumber(apsProcessSchemeDtoList.get(0).getNumber());
        return apsProcessSchemeByIdListVo;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized Boolean updateProcessScheme(List<ApsProcessSchemeParam> apsProcessSchemeParam) {
        Integer id = apsProcessSchemeParam.get(0).getId();
        ApsProcessScheme processScheme = this.getById(id);
        long count = apsProcessSchemeParam.stream().map(ApsProcessSchemeParam::getEmployeeName).distinct().count();
        Integer number = processScheme.getNumber();
        if (count != number) {
            throw new BeneWakeException("分配人数不对");
        }
        String currentProcessScheme = processScheme.getCurrentProcessScheme();
        if (processScheme.getState()) {
            //如果有效说明数据没有变动直接更新员工名
            List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeParam.stream().map(x -> {
                ApsProcessScheme apsProcessScheme = new ApsProcessScheme();
                apsProcessScheme.setId(x.getId());
                apsProcessScheme.setEmployeeName(x.getEmployeeName());

                apsProcessScheme.setState(true);
                return apsProcessScheme;
            }).collect(Collectors.toList());
            this.updateBatchById(apsProcessSchemes);
        } else {
            //数据无效 有可能是新增或删除数据 直接删除相关数据重新添加
            LambdaQueryWrapper<ApsProcessScheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            schemeLambdaQueryWrapper.eq(ApsProcessScheme::getCurrentProcessScheme, currentProcessScheme);
            remove(schemeLambdaQueryWrapper);
            List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeParam.stream().map(x -> {
                ApsProcessScheme apsProcessScheme = new ApsProcessScheme();
                apsProcessScheme.setCurrentProcessScheme(currentProcessScheme);
                apsProcessScheme.setProcessCapacityId(x.getProcessId());
                apsProcessScheme.setEmployeeName(x.getEmployeeName());
                apsProcessScheme.setNumber(number);
                apsProcessScheme.setState(true);
                return apsProcessScheme;
            }).collect(Collectors.toList());
            LambdaQueryWrapper<ApsProcessScheme> queryWrapper = new LambdaQueryWrapper<ApsProcessScheme>()
                    .eq(ApsProcessScheme::getCurrentProcessScheme, currentProcessScheme);
            remove(queryWrapper);
            saveBatch(apsProcessSchemes);
        }
        //重新计算最优方案
        saveProducSchemeManagement(apsProcessSchemeParam, number, currentProcessScheme);
        return true;
    }

    @Override
    public void downloadProcessCapacity(HttpServletResponse response, DownloadParam downloadParam) {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("我是文件名", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<ExcelProcessScheme> excelProcessSchemes = null;
            if (downloadParam.getType() == ExcelOperationEnum.CURRENT_PAGE.getCode()) {
                ApsProcessSchemeVoPage processScheme = getProcessScheme(downloadParam.getPage(), downloadParam.getSize());
                List<ApsProcessSchemeVo> apsProcessSchemeVos = processScheme.getApsProcessSchemeVos();
                excelProcessSchemes = processSchemeVoToExcelList.convert(apsProcessSchemeVos);
            } else {
                List<ApsProcessSchemeVo> apsProcessSchemeVos = apsProcessSchemeMapper.selectProcessScheme();
                excelProcessSchemes = processSchemeVoToExcelList.convert(apsProcessSchemeVos);
            }
            EasyExcel.write(response.getOutputStream() ,ExcelProcessScheme.class).sheet("sheet1").doWrite(excelProcessSchemes);
        } catch (Exception e) {
            log.error("导出基础工艺方案列表失败" + e.getMessage());
            throw new BeneWakeException(e.getMessage());
        }
    }

    @Override
    public Page selectPageLists(Page<Object> page, QueryWrapper<Object> wrapper) {
        Page<ApsProcessSchemeDto> apsProcessSchemeVoList = apsProcessSchemeMapper.selectPages(page ,wrapper);
        List<ApsProcessSchemeDto> records = apsProcessSchemeVoList.getRecords();
        List<ApsProcessSchemeVo> apsProcessSchemeVos = records.stream().map(x -> apsProcessSchemeDtoToVo.convert(x))
                .collect(Collectors.toList());

        return buildApsProcessSchemeVoPage(apsProcessSchemeVoList, apsProcessSchemeVos);
    }

    private Page<ApsProcessSchemeVo> buildApsProcessSchemeVoPage(Page<ApsProcessSchemeDto> apsProcessSchemeVoList, List<ApsProcessSchemeVo> apsProcessSchemeVos) {
        Page<ApsProcessSchemeVo> objectPage = new Page<>();
        objectPage.setRecords(apsProcessSchemeVos);
        objectPage.setTotal(apsProcessSchemeVoList.getTotal());
        objectPage.setSize(apsProcessSchemeVoList.getSize());
        objectPage.setCurrent(apsProcessSchemeVoList.getCurrent());
        return objectPage;
    }
}




