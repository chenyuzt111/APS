package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.ApsOutsourcedMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author ASUS
* @description 针对表【immediately_inventory】的数据库操作Mapper
* @createDate 2023-10-06 14:19:08
* @Entity com.benewake.system.entity.ImmediatelyInventory
*/

@Mapper
@Repository
public interface ApsOutsourcedMaterialMapper extends BaseMapper<ApsOutsourcedMaterial> {

}




