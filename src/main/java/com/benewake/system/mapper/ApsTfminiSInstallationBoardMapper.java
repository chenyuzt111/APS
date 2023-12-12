package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsTfminiSInstallationBoard;
import com.benewake.system.entity.dto.ApsTfminiSInstallationBoardDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author ASUS
* @description 针对表【aps_tfmini_s_installation_board】的数据库操作Mapper
* @createDate 2023-10-19 11:44:00
* @Entity com.benewake.system.entity.ApsTfminiSInstallationBoard
*/
@Mapper
public interface ApsTfminiSInstallationBoardMapper extends BaseMapper<ApsTfminiSInstallationBoard> {

    Page<ApsTfminiSInstallationBoardDto> selectPageList(Page page, @Param("versions") List tableVersionList);

    void insertVersionIncr();
}




