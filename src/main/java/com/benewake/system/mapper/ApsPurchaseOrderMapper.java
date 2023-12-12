package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsPurchaseOrder;
import com.benewake.system.entity.dto.ApsPurchaseOrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_purchase_order】的数据库操作Mapper
* @createDate 2023-10-26 13:58:33
* @Entity com.benewake.system.entity.ApsPurchaseOrder
*/
@Mapper
public interface ApsPurchaseOrderMapper extends BaseMapper<ApsPurchaseOrder> {

    Page<ApsPurchaseOrderDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertSelectVersionIncr();
}




