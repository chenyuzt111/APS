package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.ApsProcessCapacityParam;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.dto.ApsProcessCapacityDto;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.transfer.ApsProcessCapacityEntityToVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    @Override
    public Boolean saveOrUpdateProcessCapacityService(ApsProcessCapacityParam apsProcessCapacityVo) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        BeanUtils.copyProperties(apsProcessCapacityVo, apsProcessCapacity);
        apsProcessCapacity.setStandardTime(new BigDecimal(apsProcessCapacityVo.getStandardTime()));
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessNamePoolLambdaQueryWrapper.eq(ApsProcessNamePool::getProcessName, apsProcessCapacityVo.getProcessName());
        ApsProcessNamePool apsProcessNamePoolServiceOne = apsProcessNamePoolService.getOne(apsProcessNamePoolLambdaQueryWrapper);
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

        ApsProcessCapacity processCapacity = getById(apsProcessCapacityVo.getId());
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
        ApsProcessCapacityListVo apsProcessCapacityListVo = new ApsProcessCapacityListVo();
        apsProcessCapacityListVo.setApsProcessCapacityVo(apsProcessCapacityVos);
        apsProcessCapacityListVo.setPage(page);
        apsProcessCapacityListVo.setSize(size);
        apsProcessCapacityListVo.setTotal(total);
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
        LambdaQueryWrapper<ApsProcessCapacity> apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsProcessCapacityLambdaQueryWrapper.in(ApsProcessCapacity::getId, ids);
        List<ApsProcessCapacity> apsProcessCapacities = apsProcessCapacityMapper.selectList(apsProcessCapacityLambdaQueryWrapper);
        List<String> productFamilys = apsProcessCapacities.stream().map(ApsProcessCapacity::getProductFamily).distinct().collect(Collectors.toList());

        boolean removeBatchByIds = removeBatchByIds(ids);
        if (removeBatchByIds){
            final int[] count = new int[1];
            for (String productFamily : productFamilys) {
                count[0] = 0;
                apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
                apsProcessCapacityLambdaQueryWrapper.eq(ApsProcessCapacity::getProductFamily, productFamily)
                        .orderBy(true, true, ApsProcessCapacity::getProcessNumber);
                List<ApsProcessCapacity> apsProcessCapacityList = apsProcessCapacityMapper.selectList(apsProcessCapacityLambdaQueryWrapper);
                apsProcessCapacityList = apsProcessCapacityList.stream().peek(x -> x.setProcessNumber(++count[0])).collect(Collectors.toList());
                updateBatchById(apsProcessCapacityList);
            }
        }

        return removeBatchByIds;
    }


}




