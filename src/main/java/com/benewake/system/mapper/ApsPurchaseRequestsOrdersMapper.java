package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsPurchaseRequestsOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsPurchaseRequestsOrdersDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_purchase_requests_orders】的数据库操作Mapper
 * @createDate 2023-12-12 16:13:36
 * @Entity com.benewake.system.entity.ApsPurchaseRequestsOrders
 */
@Mapper
public interface ApsPurchaseRequestsOrdersMapper extends BaseMapper<ApsPurchaseRequestsOrders> {

    void insertVersionIncr();

    Page<ApsPurchaseRequestsOrdersDto> selectPageLists(Page page,
                                                       @Param("versions") List<VersionToChVersion> versions,
                                                       @Param(Constants.WRAPPER) QueryWrapper wrapper);

    List<ApsPurchaseRequestsOrdersDto> searchLike(@Param("versions") List versionToChVersionArrayList,
                                                  @Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}




