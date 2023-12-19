package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsMaterialDto;
import com.benewake.system.entity.dto.ApsOrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_order】的数据库操作Mapper
 * @createDate 2023-12-12 11:24:53
 * @Entity com.benewake.system.entity.ApsOrder
 */
@Mapper
public interface ApsOrderMapper extends BaseMapper<ApsOrder> {

    void insertVersionIncr();

    Page<ApsOrderDto> selectPageLists(Page page,
                                      @Param("versions") List<VersionToChVersion> versions,
                                      @Param(Constants.WRAPPER) QueryWrapper wrapper);

    List<ApsOrderDto> searchLike(@Param("versions") List versionToChVersionArrayList, @Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}




