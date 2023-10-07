package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.mapper.ApsTableVersionMapper;
import com.sun.jmx.snmp.Timestamp;
import org.springframework.stereotype.Service;

/**
* @author ASUS
* @description 针对表【aps_table_version(用于跟踪表的版本历史记录的表格)】的数据库操作Service实现
* @createDate 2023-10-07 14:28:31
*/
@Service
public class ApsTableVersionServiceImpl extends ServiceImpl<ApsTableVersionMapper, ApsTableVersion>
    implements ApsTableVersionService{


    @Override
    public Integer getMaxVersion(int code) {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.orderByDesc(ApsTableVersion::getVersionNumber)
                .last("limit 1");
        ApsTableVersion apsTableVersion = getOne(apsTableVersionLambdaQueryWrapper);
        Integer maxVersion = 0;
        if (apsTableVersion != null) {
            maxVersion = apsTableVersion.getVersionNumber();
        }
        return maxVersion;
    }

    @Override
    public void incrVersions(Integer code) {
        this.baseMapper.incrVersions(code);
    }
}




