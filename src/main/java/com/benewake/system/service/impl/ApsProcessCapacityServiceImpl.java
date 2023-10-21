package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessCapacity;
import com.benewake.system.entity.vo.ApsProcessCapacityListVo;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import com.benewake.system.service.ApsProcessCapacityService;
import com.benewake.system.mapper.ApsProcessCapacityMapper;
import com.benewake.system.transfer.ApsProcessCapacityEntityToVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author ASUS
* @description 针对表【aps_process_capacity(工序与产能表)】的数据库操作Service实现
* @createDate 2023-10-20 15:03:52
*/
@Service
public class ApsProcessCapacityServiceImpl extends ServiceImpl<ApsProcessCapacityMapper, ApsProcessCapacity>
    implements ApsProcessCapacityService{



    @Autowired
    private ApsProcessCapacityEntityToVo apsProcessCapacityEntityToVo;

    @Override
    public Boolean saveOrUpdateProcessCapacityService(ApsProcessCapacityVo apsProcessCapacityVo) {
        ApsProcessCapacity apsProcessCapacity = new ApsProcessCapacity();
        BeanUtils.copyProperties(apsProcessCapacityVo, apsProcessCapacity);
        apsProcessCapacity.setStandardTime(new BigDecimal(apsProcessCapacityVo.getStandardTime()));

        if (apsProcessCapacity.getId() == null) {
            LambdaQueryWrapper<ApsProcessCapacity> apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            apsProcessCapacityLambdaQueryWrapper
                    .eq(ApsProcessCapacity::getProductFamily , apsProcessCapacity.getProductFamily())
                    .orderByDesc(ApsProcessCapacity::getProcessNumber)
                    .last("limit 1")
                    .select(ApsProcessCapacity::getProcessNumber);
            ApsProcessCapacity processCapacity = this.getOne(apsProcessCapacityLambdaQueryWrapper);
            if (processCapacity != null) {
                apsProcessCapacity.setProcessNumber(processCapacity.getProcessNumber() + 1);
            }else {
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
        Page<ApsProcessCapacity> apsProcessCapacityPage = new Page<>(page ,size);
        LambdaQueryWrapper<ApsProcessCapacity> apsProcessCapacityLambdaQueryWrapper = new LambdaQueryWrapper<ApsProcessCapacity>()
                .orderBy(true, true, ApsProcessCapacity::getProductFamily)
                .orderBy(true, true, ApsProcessCapacity::getProcessNumber);
        Page<ApsProcessCapacity> processCapacityPage = this.getBaseMapper().selectPage(apsProcessCapacityPage, apsProcessCapacityLambdaQueryWrapper);
        List<ApsProcessCapacity> records = processCapacityPage.getRecords();
        long total = processCapacityPage.getTotal();
        HashMap<String, Integer> productFamilyToNumberMap = new HashMap<>();
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
}




