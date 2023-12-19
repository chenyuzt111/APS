package com.benewake.system.mapper;

import com.benewake.system.entity.ApsViewTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_view_table】的数据库操作Mapper
* @createDate 2023-12-06 15:29:02
* @Entity com.benewake.system.entity.ApsViewTable
*/
@Mapper
public interface ApsViewTableMapper extends BaseMapper<ApsViewTable> {

    List<String> selectColNameByViewId(@Param("viewId") Long viewId);
}




