package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.ApsOutsourcedMaterial;
import com.benewake.system.entity.Interface.ApsInventoryLockMultipleVersions;
import com.benewake.system.entity.Interface.ApsOutsourcedMaterialMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsOutsourcedMaterialDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author ASUS
* @description 针对表【immediately_inventory】的数据库操作Mapper
* @createDate 2023-10-06 14:19:08
* @Entity com.benewake.system.entity.ImmediatelyInventory
*/

@Mapper
@Repository
public interface ApsOutsourcedMaterialMapper extends BaseMapper<ApsOutsourcedMaterial> {
    List<ApsOutsourcedMaterialMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                      @Param("versions") List<VersionToChVersion> versions);

    Page<ApsOutsourcedMaterialDto> selectPageList(Page page,
                                                  @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




