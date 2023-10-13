package com.benewake.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benewake.system.entity.ApsTableVersion;
import com.benewake.system.service.ApsTableVersionService;
import com.benewake.system.mapper.ApsTableVersionMapper;
import org.springframework.stereotype.Service;

/**
* @author ASUS
* @description 针对表【aps_table_version(用于跟踪表的版本历史记录的表格)】的数据库操作Service实现
* @createDate 2023-10-12 15:46:31
*/
@Service
public class ApsTableVersionServiceImpl extends ServiceImpl<ApsTableVersionMapper, ApsTableVersion>
    implements ApsTableVersionService{


    @Override
    public Integer getMaxVersion() {
        LambdaQueryWrapper<ApsTableVersion> apsTableVersionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        apsTableVersionLambdaQueryWrapper.orderByDesc(ApsTableVersion::getVersionNumber).last("limit 1");
        ApsTableVersion maxVersionEntity = getOne(apsTableVersionLambdaQueryWrapper);
        return maxVersionEntity != null ? maxVersionEntity.getVersionNumber() : 0;
    }
}




