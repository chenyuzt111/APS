package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsInventoryLock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.ApsInventoryLockMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsInventoryLockDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_inventory_lock(用于存储库存锁定信息的表)】的数据库操作Mapper
* @createDate 2023-10-13 10:01:44
* @Entity com.benewake.system.entity.ApsInventoryLock
*/
@Mapper
public interface ApsInventoryLockMapper extends BaseMapper<ApsInventoryLock> {

    List<ApsInventoryLockMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                 @Param("versions") List<VersionToChVersion> versions);

    Page<ApsInventoryLockDto> selectPageList(Page page, @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




