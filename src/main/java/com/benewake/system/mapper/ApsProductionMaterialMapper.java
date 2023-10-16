package com.benewake.system.mapper;

import com.benewake.system.entity.ApsProductionMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.ApsOutsourcedOrderMultipleVersions;
import com.benewake.system.entity.Interface.ApsProductionMaterialMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_production_material】的数据库操作Mapper
* @createDate 2023-10-07 16:50:37
* @Entity com.benewake.system.entity.ApsProductionMaterial
*/
@Mapper
public interface ApsProductionMaterialMapper extends BaseMapper<ApsProductionMaterial> {
    List<ApsProductionMaterialMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                      @Param("versions") List<VersionToChVersion> versions);
}




