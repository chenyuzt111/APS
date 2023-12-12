package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsOutsourcedOrder;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsOutsourcedOrderDto;
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


    Page<ApsOutsourcedOrderDto> selectPageList(Page page,
                                               @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




