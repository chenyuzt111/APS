package com.benewake.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsReceiveNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.ApsPurchaseRequestMultipleVersions;
import com.benewake.system.entity.Interface.ApsReceiveNoticeMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsReceiveNoticeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_receive_notice】的数据库操作Mapper
* @createDate 2023-10-08 10:45:23
* @Entity com.benewake.system.entity.ApsReceiveNotice
*/

@Mapper
public interface ApsReceiveNoticeMapper extends BaseMapper<ApsReceiveNotice> {
    List<ApsReceiveNoticeMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                 @Param("versions") List<VersionToChVersion> versions);

    Page<ApsReceiveNoticeDto> selectPageList(Page page,
                                             @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();
}




