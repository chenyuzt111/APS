package com.benewake.system.mapper;

import com.benewake.system.entity.ApsViewColTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benewake.system.entity.ViewColumnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_view_col_table】的数据库操作Mapper
 * @createDate 2023-12-06 15:28:59
 * @Entity com.benewake.system.entity.ApsViewColTable
 */
@Mapper
public interface ApsViewColTableMapper extends BaseMapper<ApsViewColTable> {

    List<ViewColumnDto> getViewColByViewId(@Param("viewId") Long viewId, @Param("colIds") List<Integer> colIds);

}




