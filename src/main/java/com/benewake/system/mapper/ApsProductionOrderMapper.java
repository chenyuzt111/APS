package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsProductionOrder;
import com.benewake.system.entity.Interface.ApsProductionOrderMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsProductionOrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_production_order】的数据库操作Mapper
* @createDate 2023-10-07 18:22:11
* @Entity com.benewake.system.entity.ApsProductionOrder
*/
@Mapper
public interface ApsProductionOrderMapper extends BaseMapper<ApsProductionOrder> {
    List<ApsProductionOrderMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                   @Param("versions") List<VersionToChVersion> versions);

    Page<ApsProductionOrderDto> selectPageList(Page page,
                                               @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




