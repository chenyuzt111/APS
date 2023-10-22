package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessScheme;
import com.benewake.system.entity.vo.ApsProcessSchemeParam;
import com.benewake.system.entity.vo.ApsProcessSchemeParams;
import com.benewake.system.entity.vo.ApsProcessSchemeVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.service.ApsProcessSchemeService;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProcessScheme(ApsProcessSchemeParams apsProcessSchemeParams) {
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
        String currentProcessScheme = null;
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

        return saveBatch(apsProcessSchemes);
    }

    @Override
    public List<ApsProcessSchemeVo> getProcessScheme(Integer page, Integer size) {
        Integer pass = (page - 1) * size;
        return apsProcessSchemeMapper.selectProcessSchemePage(pass, size);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteProcessScheme(List<Integer> ids) {
        LambdaQueryWrapper<ApsProcessScheme> apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeLambdaQueryWrapper.in(ApsProcessScheme::getId, ids);
        List<ApsProcessScheme> apsProcessSchemes = apsProcessSchemeMapper.selectList(apsProcessSchemeLambdaQueryWrapper);
        List<String> currentProcessSchemeList = apsProcessSchemes.stream().map(ApsProcessScheme::getCurrentProcessScheme).distinct().collect(Collectors.toList());
        apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeLambdaQueryWrapper.in(ApsProcessScheme::getCurrentProcessScheme, currentProcessSchemeList);
        return remove(apsProcessSchemeLambdaQueryWrapper);
    }
}




