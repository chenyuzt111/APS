package com.benewake.system.mapper;

import com.benewake.system.entity.ApsOutsourcedOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.ApsOutsourcedMaterialMultipleVersions;
import com.benewake.system.entity.Interface.ApsOutsourcedOrderMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_outsourced_order】的数据库操作Mapper
* @createDate 2023-10-07 17:35:47
* @Entity com.benewake.system.entity.ApsOutsourcedOrder
*/
@Mapper
public interface ApsOutsourcedOrderMapper extends BaseMapper<ApsOutsourcedOrder> {

    List<ApsOutsourcedOrderMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                   @Param("versions") List<VersionToChVersion> versions);

}




