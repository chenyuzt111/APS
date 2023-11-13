package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessCapacityParam;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.ApsProcessScheme;
import com.benewake.system.entity.dto.ApsProcessCapacityDto;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.mapper.ApsProcessSchemeMapper;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.transfer.ApsProcessCapacityEntityToVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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


    @Override
    public Boolean saveOrUpdateProcessCapacityService(ApsProcessCapacityParam apsProcessCapacityParam) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        BeanUtils.copyProperties(apsProcessCapacityParam, apsProcessCapacity);
        apsProcessCapacity.setStandardTime(new BigDecimal(apsProcessCapacityParam.getStandardTime()));
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessNamePoolLambdaQueryWrapper.eq(ApsProcessNamePool::getProcessName, apsProcessCapacityParam.getProcessName());
        ApsProcessNamePool apsProcessNamePoolServiceOne = apsProcessNamePoolService.getOne(apsProcessNamePoolLambdaQueryWrapper);
        if (apsProcessNamePoolServiceOne == null) {
            return false;
        }

        apsProcessCapacity.setProcessId(apsProcessNamePoolServiceOne.getId());

        if (apsProcessCapacity.getId() == null) {
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

        ApsProcessCapacity processCapacity = getById(apsProcessCapacityParam.getId());
        apsProcessCapacity.setProcessNumber(processCapacity.getProcessNumber());
        return this.updateById(apsProcessCapacity);
    }

    @Override
    public ApsProcessCapacityListVo getAllProcessCapacity(Integer page, Integer size) {
        Integer pass = (page - 1) * size;
        List<ApsProcessCapacityDto> records = apsProcessCapacityMapper.selectPages(pass, size);
        Long total = apsProcessCapacityMapper.selectCount(null);
        Map<String, Integer> productFamilyToNumberMap = new HashMap<>();
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
        final long[] count = {apsProcessCapacityVos.stream().filter(x -> x.getProductFamily().equals(finalProductFamily)).count()};
        Integer finalOneCount = oneCount;
        //转换第一行序号
        apsProcessCapacityVos = apsProcessCapacityVos.stream().peek(x -> {
            if (x.getProductFamily().equals(finalProductFamily)) {
                x.setProcessNumber((int) (finalOneCount - count[0] + 1));
                count[0]--;
            }
        }).collect(Collectors.toList());

        ApsProcessCapacityListVo apsProcessCapacityListVo = new ApsProcessCapacityListVo();
        apsProcessCapacityListVo.setApsProcessCapacityVo(apsProcessCapacityVos);
        apsProcessCapacityListVo.setPage(page);
        apsProcessCapacityListVo.setSize(size);
        apsProcessCapacityListVo.setTotal(total);
        Long pages = total / size + 1;
        apsProcessCapacityListVo.setPages(pages);
        return apsProcessCapacityListVo;
    }

    @Override
    public List<ApsProcessCapacityVo> getProcessCapacitysByproductFamily(String productFamily) {
        List<ApsProcessCapacityVo> apsProcessCapacities = apsProcessCapacityMapper.selectProcessCapacitysByproductFamily(productFamily);
        final int[] counter = {0};
        List<ApsProcessCapacityVo> processCapacityList = apsProcessCapacities.stream().peek(x -> {
            x.setProcessNumber(++counter[0]);
        }).collect(Collectors.toList());
        return processCapacityList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchAndUpdateByIds(List<Integer> ids) {
        LambdaQueryWrapper<ApsProcessScheme> apsProcessSchemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessSchemeLambdaQueryWrapper.in(ApsProcessScheme::getProcessCapacityId, ids);
        List<ApsProcessScheme> processSchemes = apsProcessSchemeMapper.selectList(apsProcessSchemeLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(processSchemes)) {
            String process = processSchemes.stream().map(ApsProcessScheme::getCurrentProcessScheme)
                    .distinct().collect(Collectors.joining(","));
            throw new BeneWakeException(process + "还存在当前方案，请删除当前基础工艺方案后在删除");
        }

        LambdaQueryWrapper<ApsProcessCapacity> apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessCapacityLambdaQueryWrapper.in(ApsProcessCapacity::getId, ids);
        List<ApsProcessCapacity> apsProcessCapacities = apsProcessCapacityMapper.selectList(apsProcessCapacityLambdaQueryWrapper);
        List<String> productFamilys = apsProcessCapacities.stream().map(ApsProcessCapacity::getProductFamily).distinct().collect(Collectors.toList());

        boolean removeBatchByIds = removeBatchByIds(ids);
        if (removeBatchByIds) {
            ArrayList<ApsProcessCapacity> apsProcessSchemes = new ArrayList<>();
            final int[] count = new int[1];
            for (String productFamily : productFamilys) {
                count[0] = 0;
                apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
                apsProcessCapacityLambdaQueryWrapper.eq(ApsProcessCapacity::getProductFamily, productFamily)
                        .orderBy(true, true, ApsProcessCapacity::getProcessNumber);
                List<ApsProcessCapacity> apsProcessCapacityList = apsProcessCapacityMapper.selectList(apsProcessCapacityLambdaQueryWrapper);
                apsProcessCapacityList = apsProcessCapacityList.stream().peek(x -> x.setProcessNumber(++count[0])).collect(Collectors.toList());
                apsProcessSchemes.addAll(apsProcessCapacityList);
            }
            updateBatchById(apsProcessSchemes);
        }

        return removeBatchByIds;
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


}



