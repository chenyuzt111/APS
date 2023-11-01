package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.vo.ApsProcessNamePoolVo;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.mapper.ApsProcessNamePoolMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_process_name_pool】的数据库操作Service实现
 * @createDate 2023-10-20 09:20:16
 */
@Service
public class ApsProcessNamePoolServiceImpl extends ServiceImpl<ApsProcessNamePoolMapper, ApsProcessNamePool>
        implements ApsProcessNamePoolService {

    @Override
    public Boolean addOrUpdateProcess(ApsProcessNamePool apsProcessNamePool) {
        String processName = apsProcessNamePool.getProcessName();
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<ApsProcessNamePool>()
                .eq(ApsProcessNamePool::getProcessName, processName);
        ApsProcessNamePool processNamePool = this.getOne(apsProcessNamePoolLambdaQueryWrapper);
        if (processNamePool != null) {
            throw new BeneWakeException("该名称已经存在");
        }
        if (apsProcessNamePool.getId() != null) {
            return this.updateById(apsProcessNamePool);
        }
        return this.save(apsProcessNamePool);
    }

    @Override
    public ApsProcessNamePoolVo getProcess(String name, Integer page, Integer size) {
        LambdaQueryWrapper<ApsProcessNamePool> apsProcessNamePoolLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            apsProcessNamePoolLambdaQueryWrapper.like(ApsProcessNamePool::getProcessName, name);
        }
        Page<ApsProcessNamePool> apsProcessNamePoolPage = new Page<>();
        apsProcessNamePoolPage.setSize(size);
        apsProcessNamePoolPage.setCurrent(page);
        Page<ApsProcessNamePool> apsProcessNamePools = this.page(apsProcessNamePoolPage, apsProcessNamePoolLambdaQueryWrapper);
        ApsProcessNamePoolVo apsProcessNamePoolVo = new ApsProcessNamePoolVo();
        apsProcessNamePoolVo.setApsProcessNamePools(apsProcessNamePools.getRecords());
        apsProcessNamePoolVo.setPage(page);
        apsProcessNamePoolVo.setSize(size);
        apsProcessNamePoolVo.setTotal(apsProcessNamePoolPage.getTotal());
        apsProcessNamePoolVo.setPages(apsProcessNamePoolPage.getPages());
        return apsProcessNamePoolVo;
    }
}




