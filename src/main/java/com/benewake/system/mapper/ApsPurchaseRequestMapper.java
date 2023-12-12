package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsPurchaseRequest;
import com.benewake.system.entity.Interface.ApsPurchaseRequestMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsPurchaseRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_purchase_request】的数据库操作Mapper
* @createDate 2023-10-08 09:31:46
* @Entity com.benewake.system.entity.ApsPurchaseRequest
*/
@Mapper
public interface ApsPurchaseRequestMapper extends BaseMapper<ApsPurchaseRequest> {
    List<ApsPurchaseRequestMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                   @Param("versions") List<VersionToChVersion> versions);

    Page<ApsPurchaseRequestDto> selectPageList(Page page,
                                               @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




