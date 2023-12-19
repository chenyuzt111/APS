package com.benewake.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.benewake.system.entity.ApsDailyDataUpload;
import com.benewake.system.entity.dto.ApsDailyDataUploadDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author ASUS
 * @description 针对表【aps_daily_data_upload】的数据库操作Mapper
 * @createDate 2023-11-10 14:52:13
 * @Entity com.benewake.system.entity.ApsDailyDataUpload
 */
@Mapper
public interface ApsDailyDataUploadMapper extends BaseMapper<ApsDailyDataUpload> {

    Page<ApsDailyDataUploadDto> selectPageList(Page<ApsDailyDataUploadDto> uploadPage);

    @Update("CALL insert_data_into_aps_fim_request1(#{a, jdbcType=INTEGER, mode=IN})")
    void callInsertDataIntoApsFimRequest(@Param("a") int a);


    Page<ApsDailyDataUploadDto> selectPageLists(Page<Object> setSize,
                                                @Param(Constants.WRAPPER) QueryWrapper<Object> wrapper);

    List<Object> searchLike(@Param(Constants.WRAPPER) QueryWrapper<Object> queryWrapper);
}




