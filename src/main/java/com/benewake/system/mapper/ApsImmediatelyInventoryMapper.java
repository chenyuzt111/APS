package com.benewake.system.mapper;

import com.benewake.system.entity.ApsImmediatelyInventory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.ApsImmediatelyInventoryMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_immediately_inventory】的数据库操作Mapper
* @createDate 2023-10-13 10:11:17
* @Entity com.benewake.system.entity.ApsImmediatelyInventory
*/

@Mapper
public interface ApsImmediatelyInventoryMapper extends BaseMapper<ApsImmediatelyInventory> {

    List<ApsImmediatelyInventoryMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                        @Param("versions") List<VersionToChVersion> versions);
}




