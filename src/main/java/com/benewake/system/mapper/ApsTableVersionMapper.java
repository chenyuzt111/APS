package com.benewake.system.mapper;

import com.benewake.system.entity.ApsTableVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author ASUS
* @description 针对表【aps_table_version(用于跟踪表的版本历史记录的表格)】的数据库操作Mapper
* @createDate 2023-10-07 14:28:31
* @Entity com.benewake.system.entity.ApsTableVersion
*/
@Mapper
public interface ApsTableVersionMapper extends BaseMapper<ApsTableVersion> {

    void incrVersions();
}




