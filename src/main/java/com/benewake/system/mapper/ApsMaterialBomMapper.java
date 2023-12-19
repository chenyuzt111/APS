package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsMaterialBom;
import com.benewake.system.entity.dto.ApsMaterialBomDto;
import com.benewake.system.entity.kingdee.transfer.MaterialBomChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_material_bom】的数据库操作Mapper
 * @createDate 2023-10-18 15:10:19
 * @Entity com.benewake.system.entity.ApsMaterialBom
 */
@Mapper
public interface ApsMaterialBomMapper extends BaseMapper<ApsMaterialBom> {

    List<ApsMaterialBom> selectListNotDelete(@Param("deleteList") List<MaterialBomChange> deleteList);

    void insertListNotDelete(@Param("deleteList") List<MaterialBomChange> deleteList);

    Page<ApsMaterialBomDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertSelectVersionIncr();

    Page<ApsMaterialBomDto> selectPageLists(Page page,
                                            @Param("versions") List versionToChVersionArrayList,
                                            @Param(Constants.WRAPPER) QueryWrapper wrapper);

    List searchLike(@Param("versions") List versionToChVersionArrayList,
                    @Param(Constants.WRAPPER) QueryWrapper wrapper);
}




