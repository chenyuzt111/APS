package com.benewake.system.mapper;

import com.benewake.system.entity.ApsProcessCapacity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.dto.ApsProcessCapacityDto;
import com.benewake.system.entity.vo.ApsProcessCapacityVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_process_capacity(工序与产能表)】的数据库操作Mapper
* @createDate 2023-10-20 15:03:52
* @Entity com.benewake.system.entity.ApsProcessCapacity
*/
@Mapper
public interface ApsProcessCapacityMapper extends BaseMapper<ApsProcessCapacity> {

    List<ApsProcessCapacityDto> selectPages(Integer pass, Integer size);

    List<ApsProcessCapacityVo> selectProcessCapacitysByproductFamily(@Param("productFamily") String productFamily);
}




