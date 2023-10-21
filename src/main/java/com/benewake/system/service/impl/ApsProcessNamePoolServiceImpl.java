package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsProcessNamePool;
import com.benewake.system.entity.Result;
import com.benewake.system.exception.BeneWakeException;
import com.benewake.system.service.ApsProcessNamePoolService;
import com.benewake.system.mapper.ApsProcessNamePoolMapper;
import org.springframework.stereotype.Service;

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
}




