package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsImmediatelyInventory;
import com.benewake.system.entity.Interface.ApsImmediatelyInventoryMultipleVersions;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsImmediatelyInventoryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_immediately_inventory】的数据库操作Mapper
 * @createDate 2023-10-13 10:11:17
 * @Entity com.benewake.system.entity.ApsImmediatelyInventory
 */

@Mapper
public interface ApsImmediatelyInventoryMapper extends BaseMapper<ApsImmediatelyInventory> {

    List<ApsImmediatelyInventoryMultipleVersions> selectVersionPageList(@Param("pass") Integer pass, @Param("size") Integer size,
                                                                        @Param("versions") List<VersionToChVersion> versions);

    Page<ApsImmediatelyInventoryDto> selectPageList(Page<Object> objectPage,
                                                    @Param("versions") List<VersionToChVersion> versions);

    void insertSelectVersionIncr();

    Page<Object> selectPageLists(Page<Object> objectPage,
                                 @Param("versions") List<VersionToChVersion> versions,
                                 @Param(Constants.WRAPPER) QueryWrapper wrapper);

    List searchLike(@Param("versions") List versionToChVersionArrayList,
                    @Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}




