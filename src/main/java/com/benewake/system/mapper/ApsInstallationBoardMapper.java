package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsInstallationBoard;
import com.benewake.system.entity.dto.ApsInstallationBoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_installation_board】的数据库操作Mapper
* @createDate 2023-10-19 11:07:18
* @Entity com.benewake.system.entity.ApsInstallationBoard
*/
@Mapper
public interface ApsInstallationBoardMapper extends BaseMapper<ApsInstallationBoard> {

    Page<ApsInstallationBoardDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




