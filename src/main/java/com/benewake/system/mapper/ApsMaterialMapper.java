package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.Interface.VersionToChVersion;
import com.benewake.system.entity.dto.ApsMaterialDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_material】的数据库操作Mapper
 * @createDate 2023-12-11 18:04:36
 * @Entity com.benewake.system.entity.ApsMaterial
 */
@Mapper
public interface ApsMaterialMapper extends BaseMapper<ApsMaterial> {

    void insertVersionIncr();

    Page<ApsMaterialDto> selectPageLists(Page page,
                                         @Param("versions") List<VersionToChVersion> versions,
                                         @Param(Constants.WRAPPER) QueryWrapper wrapper);

    Page<ApsMaterialDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    List<ApsMaterialDto> searchLike(@Param("versions") List versionToChVersionArrayList,
                                    @Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}




