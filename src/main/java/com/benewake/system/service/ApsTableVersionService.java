package com.benewake.system.service;

import com.benewake.system.entity.ApsTableVersion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.jmx.snmp.Timestamp;

/**
* @author ASUS
* @description 针对表【aps_table_version(用于跟踪表的版本历史记录的表格)】的数据库操作Service
* @createDate 2023-10-07 14:28:31
*/
public interface ApsTableVersionService extends IService<ApsTableVersion> {

    Integer getMaxVersion(int code);

    void incrVersions(Integer code);
}
